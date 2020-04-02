package org.zipper.dtx.common.spi.plugins;

import org.zipper.dtx.common.spi.collectors.JobPluginCollector;

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