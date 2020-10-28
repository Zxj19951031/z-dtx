package org.zipper.helper.data.transport.core.enums;

public enum PluginStatus {
    READY(0),
    RUNNING(1),
    FINISH(2),
    WARN(3),
    ERROR(4),
    ;

    private final int key;

    PluginStatus(int i) {
        this.key = i;
    }

    public int getKey() {
        return key;
    }
}
