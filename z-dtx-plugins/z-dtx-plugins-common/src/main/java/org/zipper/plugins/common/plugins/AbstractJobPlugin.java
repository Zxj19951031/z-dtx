package org.zipper.plugins.common.plugins;


import org.zipper.plugins.common.collectors.JobPluginCollector;

public abstract class AbstractJobPlugin extends AbstractPlugin {

    private JobPluginCollector jobPluginCollector;

    public AbstractJobPlugin() {
    }

    public JobPluginCollector getJobPluginCollector() {
        return jobPluginCollector;
    }

    public void setJobPluginCollector(JobPluginCollector jobPluginCollector) {
        this.jobPluginCollector = jobPluginCollector;
    }

}