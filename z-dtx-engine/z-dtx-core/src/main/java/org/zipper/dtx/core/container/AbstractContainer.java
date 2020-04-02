package org.zipper.dtx.core.container;

import org.zipper.common.json.Configuration;
import org.zipper.dtx.common.spi.collectors.PluginCollector;

public abstract class AbstractContainer {
    protected Configuration allConfig;
    protected PluginCollector pluginCollector;

    public AbstractContainer(Configuration allConfig) {
        this.allConfig = allConfig;
    }

    public abstract void start();
}
