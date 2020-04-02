package org.zipper.dtx.core.container;

import org.zipper.common.exceptions.SysException;
import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.Reader;
import org.zipper.dtx.common.spi.Writer;
import org.zipper.dtx.common.spi.collectors.JobPluginCollector;
import org.zipper.dtx.common.spi.commons.CoreConstant;
import org.zipper.dtx.common.spi.commons.PluginType;
import org.zipper.dtx.common.spi.errors.CommonError;
import org.zipper.dtx.core.enums.ExecuteMode;
import org.zipper.dtx.core.schedule.AbstractScheduler;
import org.zipper.dtx.core.schedule.StandAloneScheduler;
import org.zipper.dtx.core.utils.ClassLoaderSwapper;
import org.zipper.dtx.core.utils.LoadUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JobContainer extends AbstractContainer {

    private static Logger logger = LoggerFactory.getLogger(JobContainer.class);

    private long startTimeStamp;
    private String jobId;
    private Reader.Job jobReader;
    private Writer.Job jobWriter;

    private String readerPluginName;
    private String writerPluginName;
    private ClassLoaderSwapper classLoaderSwapper = ClassLoaderSwapper
            .newCurrentThreadClassLoaderSwapper();

    private int needChannelNumber;
    private int totalStage = 1;

    public JobContainer(Configuration allConfig) {
        super(allConfig);
        this.pluginCollector = new JobPluginCollector();
    }

    @Override
    public void start() {
        logger.info("start job container");

        boolean hasException = false;

        try {
            this.startTimeStamp = System.currentTimeMillis();
            logger.debug("jobContainer starts to do init ...");
            this.init();
            logger.debug("jobContainer starts to do split ...");
            this.totalStage = this.split();
            logger.debug("jobContainer starts to do schedule ...");
            this.schedule();
            logger.debug("jobContainer starts to do destroy ...");
            this.destroy();

        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            throw SysException.newException(e);
        }
    }


    private void init() {
        this.jobId = this.allConfig.getString(CoreConstant.JOB_ID);

        //必须先Reader ，后Writer
        this.jobReader = this.initJobReader();
        this.jobWriter = this.initJobWriter();
    }

    private Reader.Job initJobReader() {
        this.readerPluginName = this.allConfig.getString(CoreConstant.JOB_CONTENT_READER_NAME);

        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.READER, this.readerPluginName));

        Reader.Job jobReader = (Reader.Job) LoadUtil.loadJobPlugin(
                PluginType.READER, this.readerPluginName);

        // 设置reader的jobConfig
        jobReader.setPluginJobConf(this.allConfig.getConfiguration(
                CoreConstant.JOB_CONTENT_READER_PARAMETER));


        jobReader.setJobPluginCollector((JobPluginCollector) this.pluginCollector);
        jobReader.init();

        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return jobReader;
    }

    private Writer.Job initJobWriter() {
        this.writerPluginName = this.allConfig.getString(
                CoreConstant.JOB_CONTENT_WRITER_NAME);
        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.WRITER, this.writerPluginName));

        Writer.Job jobWriter = (Writer.Job) LoadUtil.loadJobPlugin(
                PluginType.WRITER, this.writerPluginName);

        // 设置writer的jobConfig
        jobWriter.setPluginJobConf(this.allConfig.getConfiguration(
                CoreConstant.JOB_CONTENT_WRITER_PARAMETER));

        jobWriter.setJobPluginCollector((JobPluginCollector) this.pluginCollector);
        jobWriter.init();

        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return jobWriter;
    }

    private int split() {
        this.adjustChannelNumber();

        List<Configuration> readerTaskConfigs = this.doReaderSplit(this.needChannelNumber);

        int taskNumber = readerTaskConfigs.size();

        List<Configuration> writerTaskConfigs = this.doWriterSplit(taskNumber);

        // TODO: 2020/2/14 转换器配置List
        List<Configuration> transformerList = null;

        List<Configuration> contentConfig = mergeReaderAndWriterTaskConfigs(
                readerTaskConfigs, writerTaskConfigs, null);
        logger.debug("contentConfig configuration: " + JSON.toJSONString(contentConfig));

        this.allConfig.set(CoreConstant.JOB_CONTENT, contentConfig);
        return contentConfig.size();
    }

    /**
     * 计算理论通道数
     */
    private void adjustChannelNumber() {
        // TODO: 2020/2/14 依据bps和tps来计算合理通道数

        boolean isChannelLimit = (this.allConfig.getInt(
                CoreConstant.JOB_SETTING_SPEED_CHANNEL, 0) > 0);
        if (isChannelLimit) {
            this.needChannelNumber = this.allConfig.getInt(
                    CoreConstant.JOB_SETTING_SPEED_CHANNEL);

            logger.info("Job set Channel-Number to " + this.needChannelNumber + " channels.");
            return;
        }

        throw SysException.newException(
                CommonError.CONFIG_ERROR, "Job运行速度必须设置");
    }

    private List<Configuration> doReaderSplit(int adviceNumber) {
        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.READER, this.readerPluginName));
        List<Configuration> readerSlicesConfigs = this.jobReader.split(adviceNumber);
        if (readerSlicesConfigs == null || readerSlicesConfigs.size() <= 0) {
            throw SysException.newException(
                    CommonError.PLUGIN_SPLIT_ERROR,
                    "reader切分的task数目不能小于等于0");
        }
        logger.info("Reader.Job [{}] splits to [{}] tasks.",
                this.readerPluginName, readerSlicesConfigs.size());
        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return readerSlicesConfigs;
    }

    private List<Configuration> doWriterSplit(int readerTaskNumber) {
        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.WRITER, this.writerPluginName));

        List<Configuration> writerSlicesConfigs = this.jobWriter
                .split(readerTaskNumber);
        if (writerSlicesConfigs == null || writerSlicesConfigs.size() <= 0) {
            throw SysException.newException(
                    CommonError.PLUGIN_SPLIT_ERROR,
                    "writer切分的task不能小于等于0");
        }
        logger.info("Writer.Job [{}] splits to [{}] tasks.",
                this.writerPluginName, writerSlicesConfigs.size());
        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return writerSlicesConfigs;
    }

    /**
     * 合并读写，转换器的配置
     *
     * @param readerTasksConfigs
     * @param writerTasksConfigs
     * @param transformerConfigs
     * @return
     */
    private List<Configuration> mergeReaderAndWriterTaskConfigs(
            List<Configuration> readerTasksConfigs,
            List<Configuration> writerTasksConfigs,
            List<Configuration> transformerConfigs) {
        if (readerTasksConfigs.size() != writerTasksConfigs.size()) {
            throw SysException.newException(
                    CommonError.PLUGIN_SPLIT_ERROR,
                    String.format("reader切分的task数目[%d]不等于writer切分的task数目[%d].",
                            readerTasksConfigs.size(), writerTasksConfigs.size())
            );
        }

        List<Configuration> contentConfigs = new ArrayList<Configuration>();
        for (int i = 0; i < readerTasksConfigs.size(); i++) {
            Configuration taskConfig = Configuration.newDefault();
            taskConfig.set(CoreConstant.JOB_READER_NAME,
                    this.readerPluginName);
            taskConfig.set(CoreConstant.JOB_READER_PARAMETER,
                    readerTasksConfigs.get(i));
            taskConfig.set(CoreConstant.JOB_WRITER_NAME,
                    this.writerPluginName);
            taskConfig.set(CoreConstant.JOB_WRITER_PARAMETER,
                    writerTasksConfigs.get(i));

            if (transformerConfigs != null && transformerConfigs.size() > 0) {
                taskConfig.set(CoreConstant.JOB_TRANSFORMER, transformerConfigs);
            }

            taskConfig.set(CoreConstant.TASK_ID, jobId + "-" + i);
            contentConfigs.add(taskConfig);
        }

        return contentConfigs;
    }

    private void schedule() {

        ExecuteMode mode = ExecuteMode.STANDALONE;

        AbstractScheduler scheduler = null;
        if (ExecuteMode.STANDALONE == mode) {
            logger.info("Running by {} Mode.", mode);

            scheduler = new StandAloneScheduler();

            scheduler.schedule(this.mergeAllConfigToTaskConfig());
        } else if (ExecuteMode.DISTRIBUTE == mode) {
            // TODO: 2020/2/14 分布式调度
        }
    }

    private List<Configuration> mergeAllConfigToTaskConfig() {
        List<Configuration> taskConfigs = this.allConfig.getListConfiguration(CoreConstant.JOB_CONTENT);
        for (Configuration taskConfig : taskConfigs) {
            taskConfig.set(CoreConstant.TUNNEL, this.allConfig.getConfiguration(CoreConstant.TUNNEL));
        }
        return taskConfigs;
    }

    private void destroy() {
        this.jobReader.destroy();
        this.jobWriter.destroy();
    }
}
