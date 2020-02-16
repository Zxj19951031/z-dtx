package cn.com.citydo.dtx.common.spi.plugins;

import cn.com.citydo.dtx.common.spi.collectors.JobPluginCollector;

public abstract class AbstractJobPlugin extends AbstractPlugin {

    private JobPluginCollector jobPluginCollector;

    public JobPluginCollector getJobPluginCollector() {
        return jobPluginCollector;
    }

    public void setJobPluginCollector(JobPluginCollector jobPluginCollector) {
        this.jobPluginCollector = jobPluginCollector;
    }

}