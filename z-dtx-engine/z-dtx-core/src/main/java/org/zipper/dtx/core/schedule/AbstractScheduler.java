package org.zipper.dtx.core.schedule;

import org.zipper.common.json.Configuration;

import java.util.List;

/**
 * task调度器，调度策略包括本地调度，分布式调度
 */
public abstract class AbstractScheduler {

    /**
     * 不同的调度器实现具体调度逻辑
     *
     * @param taskConfigs
     */
    public abstract void schedule(List<Configuration> taskConfigs);

}
