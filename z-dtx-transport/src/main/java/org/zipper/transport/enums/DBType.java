package org.zipper.transport.enums;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.TypeError;

import java.io.IOException;
import java.lang.reflect.Type;


public enum DBType implements JSONSerializable {
    MySql(1, "com.mysql.jdbc.Driver"),
    Oracle(2, "oracle.jdbc.driver.OracleDriver");
    public int type;
    public String className;

    DBType(int type, String className) {
        this.type = type;
        this.className = className;
    }

    public static DBType get(int type) {
        for (DBType element : DBType.values()) {
            if (element.type == type) {
                return element;
            }
        }

        throw SysException.newException(TypeError.UNKNOWN_TYPE,
                String.format("不支持的枚举值[%s]，请联系管理员", type));
    }

    @Override
    public void write(JSONSerializer serializer, Object o, Type type, int i) throws IOException {
        serializer.write(this.type);
    }
}
