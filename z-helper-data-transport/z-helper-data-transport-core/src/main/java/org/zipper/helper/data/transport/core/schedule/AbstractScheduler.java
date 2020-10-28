package org.zipper.helper.data.transport.core.schedule;

import org.zipper.helper.data.transport.common.collectors.JobPluginCollector;
import org.zipper.helper.util.json.JsonObject;

import java.util.List;

/**
 * task调度器，调度策略包括本地调度，分布式调度
 *
 * @author zhuxj
 */
public abstract class AbstractScheduler {

    protected JobPluginCollector collector;
    protected int jobSleepIntervalInMillSec;

    /**
     * 不同的调度器实现具体调度逻辑
     *
     * @param taskConfigs
     */
    public abstract void schedule(List<JsonObject> taskConfigs);

    /**
     * 收集所有task任务，并进行信息汇报
     */
    public abstract void report();

    public void setCollector(JobPluginCollector collector) {
        this.collector = collector;
    }

    public void setJobSleepIntervalInMillSec(int jobSleepIntervalInMillSec) {
        this.jobSleepIntervalInMillSec = jobSleepIntervalInMillSec;
    }
}
