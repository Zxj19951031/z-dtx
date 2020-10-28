package org.zipper.helper.data.transport.core.container;


import org.zipper.helper.data.transport.common.Reader;
import org.zipper.helper.data.transport.common.Writer;
import org.zipper.helper.data.transport.common.collectors.TaskPluginCollector;
import org.zipper.helper.data.transport.common.commons.CoreConstant;
import org.zipper.helper.data.transport.common.commons.PluginType;
import org.zipper.helper.data.transport.common.tunnels.BufferTunnel;
import org.zipper.helper.data.transport.core.enums.PluginStatus;
import org.zipper.helper.data.transport.core.runner.ReaderRunner;
import org.zipper.helper.data.transport.core.runner.WriterRunner;
import org.zipper.helper.data.transport.core.utils.ClassLoaderSwapper;
import org.zipper.helper.data.transport.core.utils.LoadUtil;
import org.zipper.helper.util.json.JsonObject;

/**
 * task容器
 *
 * @author zhuxj
 */
public class TaskContainer extends AbstractContainer {

    /**
     * 读取线程
     */
    private Thread readerThread;
    /**
     * 写入线程
     */
    private Thread writerThread;
    /**
     * 读取任务
     */
    private ReaderRunner readerRunner;
    /**
     * 写入任务
     */
    private WriterRunner writerRunner;
    /**
     * 传输通道
     */
    private final BufferTunnel tunnel;
    private Reader.Task taskReader;
    private Writer.Task taskWriter;
    private String taskId;

    private String readerPluginName;
    private String writerPluginName;


    private final ClassLoaderSwapper classLoaderSwapper = ClassLoaderSwapper
            .newCurrentThreadClassLoaderSwapper();

    public TaskContainer(JsonObject taskConfig) {
        super(taskConfig);
        this.pluginCollector = new TaskPluginCollector();
        this.tunnel = new BufferTunnel(taskConfig);
        this.tunnel.setPluginCollector((TaskPluginCollector) this.pluginCollector);
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

        jobReader.setPluginJobConf(this.allConfig.getJsonObject(
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
        jobWriter.setPluginJobConf(this.allConfig.getJsonObject(
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
                && writerRunner.getStatus().getKey() > PluginStatus.RUNNING.getKey()) {
            return readerRunner.getStatus().getKey() > writerRunner.getStatus().getKey()
                    ? readerRunner.getStatus() : writerRunner.getStatus();
        } else {
            return readerRunner.getStatus().getKey() < writerRunner.getStatus().getKey()
                    ? readerRunner.getStatus() : writerRunner.getStatus();
        }
    }
}
