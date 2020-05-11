package org.zipper.transport.enums;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.TypeError;

import java.io.IOException;
import java.lang.reflect.Type;


public enum DBType implements JSONSerializable {
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

    @Override
    public void write(JSONSerializer serializer, Object o, Type type, int i) throws IOException {
        serializer.write(this.type);
    }
}
