package cn.com.citydo.dtx.common.spi.plugins;

import cn.com.citydo.consts.json.Configuration;
import lombok.Data;

@Data
public abstract class AbstractPlugin {
    private String pluginName;
    private Configuration allConfig;

    public AbstractPlugin(Configuration allConfig) {
        this.allConfig = allConfig;
    }

    public abstract void init();

    public abstract void prepare();

    public abstract void post();

    public abstract void destroy();

}
