package org.zipper.transport.enums;

import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.TypeError;

public enum DBInfo {

    CATALOG("catalog"),
    SCHEMA("schema"),
    TABLE("table"),
    COLUMN("column");
    String tag;

    DBInfo(String tag) {
        this.tag = tag;
    }

    public static DBInfo get(String type) {
        for (DBInfo element : DBInfo.values()) {
            if (element.tag.equals(type)) {
                return element;
            }
        }

        throw SysException.newException(TypeError.UNKNOWN_TYPE,
                String.format("不支持的枚举值:%s，请联系管理员", type));
    }
}
