package cn.com.citydo.dtx.core.container;

import cn.com.citydo.consts.json.Configuration;
import cn.com.citydo.dtx.common.spi.Reader;
import cn.com.citydo.dtx.common.spi.Writer;
import cn.com.citydo.dtx.common.spi.collectors.TaskPluginCollector;
import cn.com.citydo.dtx.common.spi.commons.CoreConstant;
import cn.com.citydo.dtx.common.spi.commons.PluginType;
import cn.com.citydo.dtx.common.spi.tunnels.BufferTunnel;
import cn.com.citydo.dtx.core.enums.PluginStatus;
import cn.com.citydo.dtx.core.runner.ReaderRunner;
import cn.com.citydo.dtx.core.runner.WriterRunner;
import cn.com.citydo.dtx.core.utils.ClassLoaderSwapper;
import cn.com.citydo.dtx.core.utils.LoadUtil;

public class TaskContainer extends AbstractContainer {

    private Thread readerThread;
    private Thread writerThread;
    private ReaderRunner readerRunner;
    private WriterRunner writerRunner;

    private BufferTunnel tunnel;
    private Reader.Task taskReader;
    private Writer.Task taskWriter;
    private String taskId;

    private String readerPluginName;
    private String writerPluginName;


    private ClassLoaderSwapper classLoaderSwapper = ClassLoaderSwapper
            .newCurrentThreadClassLoaderSwapper();

    public TaskContainer(Configuration taskConfig) {
        super(taskConfig);
        this.tunnel = new BufferTunnel(taskConfig);
        this.pluginCollector = new TaskPluginCollector();
    }

    @Override
    public void start() {
        this.taskId = this.allConfig.getString(CoreConstant.TASK_ID);

        this.taskReader = this.initTaskReader();
        this.taskWriter = this.initTaskWriter();

        this.readerRunner = new ReaderRunner(this.tunnel, this.taskReader);
        this.writerRunner = new WriterRunner(this.tunnel, this.taskWriter);

        this.readerThread = new Thread(readerRunner);
        this.readerThread.setName("reader-" + taskId);
        this.writerThread = new Thread(writerRunner);
        this.writerThread.setName("writer-" + taskId);

        this.readerThread.start();
        this.writerThread.start();
    }

    private Reader.Task initTaskReader() {
        this.readerPluginName = this.allConfig.getString(CoreConstant.JOB_READER_NAME);

        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.READER, this.readerPluginName));

        Reader.Task jobReader = (Reader.Task) LoadUtil.loadTaskPlugin(
                PluginType.READER, this.readerPluginName);

        jobReader.setPluginJobConf(this.allConfig.getConfiguration(
                CoreConstant.JOB_READER_PARAMETER));

        jobReader.setTaskPluginCollector((TaskPluginCollector) this.pluginCollector);

        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return jobReader;
    }

    private Writer.Task initTaskWriter() {
        this.writerPluginName = this.allConfig.getString(CoreConstant.JOB_WRITER_NAME);
        classLoaderSwapper.setCurrentThreadClassLoader(LoadUtil.getJarLoader(
                PluginType.WRITER, this.writerPluginName));

        Writer.Task jobWriter = (Writer.Task) LoadUtil.loadTaskPlugin(
                PluginType.WRITER, this.writerPluginName);

        // 设置writer的jobConfig
        jobWriter.setPluginJobConf(this.allConfig.getConfiguration(
                CoreConstant.JOB_WRITER_PARAMETER));

        jobWriter.setTaskPluginCollector((TaskPluginCollector) this.pluginCollector);

        classLoaderSwapper.restoreCurrentThreadClassLoader();
        return jobWriter;
    }

    public String getTaskId() {
        return taskId;
    }

    public PluginStatus getStatus() {
        //如果读写都已不再运行则取较大值,否则较小值
        if (readerRunner.getStatus().getKey() > PluginStatus.RUNNING.getKey()
                && writerRunner.getStatus().getKey() > PluginStatus.RUNNING.getKey())
            return readerRunner.getStatus().getKey() > writerRunner.getStatus().getKey()
                    ? readerRunner.getStatus() : writerRunner.getStatus();
        else
            return readerRunner.getStatus().getKey() < writerRunner.getStatus().getKey()
                    ? readerRunner.getStatus() : writerRunner.getStatus();
    }
}
