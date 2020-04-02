package cn.com.citydo.dtx.core.container;

import cn.com.citydo.common.json.Configuration;
import cn.com.citydo.dtx.common.spi.collectors.PluginCollector;

public abstract class AbstractContainer {
    protected Configuration allConfig;
    protected PluginCollector pluginCollector;

    public AbstractContainer(Configuration allConfig) {
        this.allConfig = allConfig;
    }

    public abstract void start();
}
