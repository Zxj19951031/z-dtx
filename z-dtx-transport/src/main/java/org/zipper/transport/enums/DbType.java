package org.zipper.transport.enums;

import com.alibaba.fastjson.serializer.JSONSerializable;
import com.alibaba.fastjson.serializer.JSONSerializer;
import org.zipper.helper.exception.ErrorCode;
import org.zipper.helper.exception.HelperException;
import org.zipper.transport.pojo.entity.Transport;

import java.io.IOException;
import java.lang.reflect.Type;


/**
 * @author zhuxj
 */

public enum DbType implements JSONSerializable {
    MySql(1, "com.mysql.jdbc.Driver"),
    Oracle(2, "oracle.jdbc.driver.OracleDriver");

    /**
     * 枚举值
     */
    public int type;
    /**
     * 数据库驱动名称
     */
    public String className;

    DbType(int type, String className) {
        this.type = type;
        this.className = className;
    }

    public static DbType get(int type) {
        for (DbType element : DbType.values()) {
            if (element.type == type) {
                return element;
            }
        }

        throw HelperException.newException(ErrorCode.UNKNOWN_TYPE,
                String.format("不支持的枚举值[%s]，请联系管理员", type));
    }

    @Override
    public void write(JSONSerializer serializer, Object o, Type type, int i) throws IOException {
        serializer.write(this.type);
    }
}
