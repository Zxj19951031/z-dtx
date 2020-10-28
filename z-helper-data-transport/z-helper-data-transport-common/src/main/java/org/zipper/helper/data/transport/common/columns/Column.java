package org.zipper.helper.data.transport.common.columns;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public abstract class Column {
    //支持的列类型
    public enum Type {
        BAD, NULL, INT, LONG, DOUBLE, STRING, BOOL, DATE, BYTES, BIGDecimal
    }

    private Type type;      //字段类型
    private int byteSize;   //字段长度
    private Object data;    //字段内容

    public Column(Type type, int byteSize, Object data) {
        this.type = type;
        this.byteSize = byteSize;
        this.data = data;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


    public abstract Long asLong();

    public abstract Double asDouble();

    public abstract String asString();

    public abstract Date asDate();

    public abstract byte[] asBytes();

    public abstract Boolean asBoolean();

    public abstract BigDecimal asBigDecimal();

    public abstract BigInteger asBigInteger();

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getByteSize() {
        return byteSize;
    }

    public void setByteSize(int byteSize) {
        this.byteSize = byteSize;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
