package org.zipper.plugins.core.container;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zipper.plugins.common.Reader;
import org.zipper.plugins.common.Writer;
import org.zipper.plugins.common.collectors.JobPluginCollector;
import org.zipper.plugins.common.commons.CoreConstant;
import org.zipper.plugins.common.commons.PluginType;
import org.zipper.plugins.common.errors.CommonError;
import org.zipper.plugins.core.container.AbstractContainer;
import org.zipper.plugins.core.enums.ExecuteMode;
import org.zipper.plugins.core.schedule.AbstractScheduler;
import org.zipper.plugins.core.schedule.StandAloneScheduler;
import org.zipper.plugins.core.utils.ClassLoaderSwapper;
import org.zipper.plugins.core.utils.LoadUtil;
import org.zipper.helper.exception.HelperException;
import org.zipper.helper.util.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class JobContainer extends AbstractContainer {

    private static final Logger log = LoggerFactory.getLogger(JobContainer.class);

    private String jobId;
    private Reader.Job jobReader;
    private Writer.Job jobWriter;

    private String readerPluginName;
    private String writerPluginName;
    private final ClassLoaderSwapper classLoaderSwapper = ClassLoaderSwapper
            .newCurrentThreadClassLoaderSwapper();

    private int needChannelNumber;
    private int totalStage = 1;

    public JobContainer(JsonObject allConfig) {
        super(allConfig);
        this.pluginCollector = new JobPluginCollector();
    }

    @Override
    public void start() {
        log.info("start job container");

        boolean hasException = false;

        try {
            log.debug("jobContainer starts to do init ...");
            this.init();
            log.debug("jobContainer starts to do split ...");
            this.totalStage = this.split();
            log.debug("jobContainer starts to do schedule ...");
            this.schedule();
            log.debug("jobContainer starts to do destroy ...");
            this.destroy();

        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw HelperException.newException(e);
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
        jobReader.setPluginJobConf(this.allConfig.getJsonObject(
                CoreConstant.JOB_CONTENT_READER_PARAMETER));


        jobReader.setJobPluginCollector((JobPluginCollector) this.pluginCollector);
        jobReader.init();
        log.info("job reader init ok");


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
        jobWriter.setPluginJobConf(this.allConfig.getJsonObject(
                CoreConstant.JOB_CONTENT_WRITER_PARAMETER));

        jobWriter.setJobPluginCollector((JobPluginCollector) this.pluginCollector);
        jobWriter.init();
        log.info("job writer init ok");

        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return jobWriter;
    }

    private int split() {
        this.adjustChannelNumber();

        List<JsonObject> readerTaskConfigs = this.doReaderSplit(this.needChannelNumber);

        int taskNumber = readerTaskConfigs.size();

        List<JsonObject> writerTaskConfigs = this.doWriterSplit(taskNumber);

        // TODO: 2020/2/14 转换器配置List
        List<JsonObject> transformerList = null;

        List<JsonObject> contentConfig = mergeReaderAndWriterTaskConfigs(
                readerTaskConfigs, writerTaskConfigs, null);
        log.debug("contentConfig configuration: " + JSON.toJSONString(contentConfig));

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

            log.info("Job set Channel-Number to " + this.needChannelNumber + " channels.");
            return;
        }

        throw HelperException.newException(
                CommonError.CONFIG_ERROR, "Job运行速度必须设置");
    }

    private List<JsonObject> doReaderSplit(int adviceNumber) {
        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.READER, this.readerPluginName));
        List<JsonObject> readerSlicesConfigs = this.jobReader.split(adviceNumber);
        if (readerSlicesConfigs == null || readerSlicesConfigs.size() <= 0) {
            throw HelperException.newException(
                    CommonError.PLUGIN_SPLIT_ERROR,
                    "reader切分的task数目不能小于等于0");
        }
        log.info("Reader.Job [{}] splits to [{}] tasks.",
                this.readerPluginName, readerSlicesConfigs.size());
        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return readerSlicesConfigs;
    }

    private List<JsonObject> doWriterSplit(int readerTaskNumber) {
        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.WRITER, this.writerPluginName));

        List<JsonObject> writerSlicesConfigs = this.jobWriter
                .split(readerTaskNumber);
        if (writerSlicesConfigs == null || writerSlicesConfigs.size() <= 0) {
            throw HelperException.newException(
                    CommonError.PLUGIN_SPLIT_ERROR,
                    "writer切分的task不能小于等于0");
        }
        log.info("Writer.Job [{}] splits to [{}] tasks.",
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
    private List<JsonObject> mergeReaderAndWriterTaskConfigs(
            List<JsonObject> readerTasksConfigs,
            List<JsonObject> writerTasksConfigs,
            List<JsonObject> transformerConfigs) {
        if (readerTasksConfigs.size() != writerTasksConfigs.size()) {
            throw HelperException.newException(
                    CommonError.PLUGIN_SPLIT_ERROR,
                    String.format("reader切分的task数目[%d]不等于writer切分的task数目[%d].",
                            readerTasksConfigs.size(), writerTasksConfigs.size())
            );
        }

        List<JsonObject> contentConfigs = new ArrayList<JsonObject>();
        for (int i = 0; i < readerTasksConfigs.size(); i++) {
            JsonObject taskConfig = JsonObject.newDefault();
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
            log.info("Running by {} Mode.", mode);

            scheduler = new StandAloneScheduler();
            scheduler.setJobSleepIntervalInMillSec(allConfig.getInt(CoreConstant.JOB_SETTING_REPORT_INTERVAL, 3000));
            scheduler.setCollector((JobPluginCollector) getPluginCollector());
            scheduler.schedule(this.mergeAllConfigToTaskConfig());
        } else if (ExecuteMode.DISTRIBUTE == mode) {
            // TODO: 2020/2/14 分布式调度
        }
    }

    private List<JsonObject> mergeAllConfigToTaskConfig() {
        List<JsonObject> taskConfigs = this.allConfig.getListJsonObject(CoreConstant.JOB_CONTENT);
        for (JsonObject taskConfig : taskConfigs) {
            taskConfig.set(CoreConstant.JOB_TUNNEL, this.allConfig.getJsonObject(CoreConstant.JOB_TUNNEL));
        }
        return taskConfigs;
    }

    private void destroy() {
        this.jobReader.destroy();
        log.info("job reader destroy ok");
        this.jobWriter.destroy();
        log.info("job writer destroy ok");

    }
}
