package org.zipper.plugins.core.runner;


import org.zipper.plugins.core.enums.PluginStatus;

public abstract class AbstractRunner {

    private PluginStatus status;

    public AbstractRunner() {
        status = PluginStatus.READY;
    }

    public void running() {
        status = PluginStatus.RUNNING.getKey() > status.getKey() ? PluginStatus.RUNNING : status;
    }

    public void warn() {
        status = PluginStatus.WARN.getKey() > status.getKey() ? PluginStatus.WARN : status;
    }

    public void error() {
        status = PluginStatus.ERROR.getKey() > status.getKey() ? PluginStatus.ERROR : status;
    }

    public void finish() {
        status = PluginStatus.FINISH.getKey() > status.getKey() ? PluginStatus.FINISH : status;
    }

    public PluginStatus getStatus() {
        return status;
    }
}
