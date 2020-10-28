package org.zipper.helper.data.transport.common.plugins;


import org.zipper.helper.data.transport.common.collectors.JobPluginCollector;

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