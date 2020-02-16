package cn.com.citydo.dtx.common.spi.plugins;

import cn.com.citydo.consts.json.Configuration;
import lombok.Data;

@Data
public abstract class AbstractPlugin {
    private String pluginName;
    private Configuration pluginConfig;
    private Configuration allConfig;

    public abstract void init();

    public abstract void prepare();

    public abstract void post();

    public abstract void destroy();

    public void setPluginConf(Configuration pluginConf) {
        this.pluginConfig = pluginConf;
    }

    public void setPluginJobConf(Configuration jobConf) {
        this.allConfig = jobConf;
    }
}
