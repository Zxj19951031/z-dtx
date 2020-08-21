package org.zipper.transport.enums;

import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;

/**
 * @author zhuxj
 */

public enum DbInfo {

    CATALOG("catalog"),
    SCHEMA("schema"),
    TABLE("table"),
    COLUMN("column");

    private final String tag;

    DbInfo(String tag) {
        this.tag = tag;
    }

    public static DbInfo get(String type) {
        for (DbInfo element : DbInfo.values()) {
            if (element.tag.equals(type)) {
                return element;
            }
        }

        throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                String.format("不支持的枚举值:%s，请联系管理员", type));
    }
}
