package org.zipper.plugins.common.commons;

public enum PluginType {
    READER("reader"),
    TRANSFORMER("transformer"),
    WRITER("writer"),
    HANDLER("handler");

    private final String pluginType;

    PluginType(String pluginType) {
        this.pluginType = pluginType;
    }

    @Override
    public String toString() {
        return this.pluginType;
    }
}