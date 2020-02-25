package cn.com.citydo.dtx.common.spi.plugins;

import cn.com.citydo.consts.json.Configuration;
import lombok.Data;

@Data
public abstract class AbstractPlugin {
    private String pluginName;
    private Configuration pluginConfig;
    private Configuration allConfig;

    public AbstractPlugin() {
    }

    public abstract void init();

    public void prepare() {

    }

    public void post() {

    }

    public abstract void destroy();

    public void setPluginConf(Configuration pluginConf) {
        this.pluginConfig = pluginConf;
    }

    public void setPluginJobConf(Configuration jobConf) {
        this.allConfig = jobConf;
    }
}
