package org.zipper.db.enums;

import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.TypeError;

public enum DBType {
    MySql(1), Oracle(2);
    private int type;

    DBType(int type) {
        this.type = type;
    }

    public static DBType get(int val) {
        for (DBType value : DBType.values()) {
            if (value.type == val) {
                return value;
            }
        }

        throw SysException.newException(TypeError.UNKNOWN_TYPE, "DBType不支持的枚举值");
    }
}