package cn.com.citydo.dtx.core.container;

import cn.com.citydo.dtx.core.collectors.PluginCollector;
import cn.com.citydo.consts.json.Configuration;

public abstract class AbstractContainer {
    protected Configuration allConfig;
    protected PluginCollector pluginCollector;

    public AbstractContainer(Configuration allConfig) {
        this.allConfig = allConfig;
    }

    public abstract void start();
}
