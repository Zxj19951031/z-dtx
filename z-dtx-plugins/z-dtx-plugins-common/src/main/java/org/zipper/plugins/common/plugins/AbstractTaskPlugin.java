package org.zipper.plugins.common.plugins;


import org.zipper.plugins.common.collectors.TaskPluginCollector;

/**
 * 抽象的task插件，所有读写插件task类均继承该类
 * 内部维护了一个task插件消息收集器
 *
 * @author zhuxj
 */
public abstract class AbstractTaskPlugin extends AbstractPlugin {

    /**
     * 信息收集器
     */
    private TaskPluginCollector taskPluginCollector;

    public TaskPluginCollector getTaskPluginCollector() {
        return taskPluginCollector;
    }

    public void setTaskPluginCollector(TaskPluginCollector taskPluginCollector) {
        this.taskPluginCollector = taskPluginCollector;
    }
}