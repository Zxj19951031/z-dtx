package cn.com.citydo.dtx.common.spi.commons;

public enum PluginType {
    READER("reader"),
    TRANSFORMER("transformer"),
    WRITER("writer"),
    HANDLER("handler");

    private String pluginType;

    private PluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    @Override
    public String toString() {
        return this.pluginType;
    }
}