package org.zipper.dtx.common.spi.plugins;

import org.zipper.dtx.common.spi.collectors.TaskPluginCollector;

public abstract class AbstractTaskPlugin extends AbstractPlugin {

    private TaskPluginCollector taskPluginCollector;

    public TaskPluginCollector getTaskPluginCollector() {
        return taskPluginCollector;
    }

    public void setTaskPluginCollector(TaskPluginCollector taskPluginCollector) {
        this.taskPluginCollector = taskPluginCollector;
    }
}