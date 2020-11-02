package org.zipper.plugins.common.plugins;

import org.zipper.helper.util.json.JsonObject;


/**
 * 顶层插件抽象
 *
 * @author zhuxj
 */
public abstract class AbstractPlugin {
    /**
     * 插件名称
     */
    private String pluginName;
    /**
     * 插件配置
     */
    private JsonObject pluginConfig;
    /**
     * 全局配置
     */
    private JsonObject allConfig;

    public AbstractPlugin() {
    }

    public abstract void init();

    public void prepare() {

    }

    public void post() {

    }

    public abstract void destroy();

    public void setPluginConf(JsonObject pluginConf) {
        this.pluginConfig = pluginConf;
    }

    public void setPluginJobConf(JsonObject jobConf) {
        this.allConfig = jobConf;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public JsonObject getPluginConfig() {
        return pluginConfig;
    }

    public void setPluginConfig(JsonObject pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public JsonObject getAllConfig() {
        return allConfig;
    }

    public void setAllConfig(JsonObject allConfig) {
        this.allConfig = allConfig;
    }
}
