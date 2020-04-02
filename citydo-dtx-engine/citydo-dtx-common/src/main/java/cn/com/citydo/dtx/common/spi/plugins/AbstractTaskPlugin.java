package cn.com.citydo.dtx.common.spi.plugins;

import cn.com.citydo.common.json.Configuration;
import cn.com.citydo.dtx.common.spi.collectors.TaskPluginCollector;

public abstract class AbstractTaskPlugin extends AbstractPlugin {

    private TaskPluginCollector taskPluginCollector;

    public TaskPluginCollector getTaskPluginCollector() {
        return taskPluginCollector;
    }

    public void setTaskPluginCollector(TaskPluginCollector taskPluginCollector) {
        this.taskPluginCollector = taskPluginCollector;
    }
}