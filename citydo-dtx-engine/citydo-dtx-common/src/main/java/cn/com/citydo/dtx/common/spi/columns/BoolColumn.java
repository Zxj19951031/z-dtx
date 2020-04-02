package cn.com.citydo.dtx.common.spi.columns;

import cn.com.citydo.common.exceptions.SysException;
import cn.com.citydo.dtx.common.spi.errors.ColumnError;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class BoolColumn extends Column {


    public BoolColumn(Boolean bool) {
        super(Type.BOOL, 1, bool);
    }

    public BoolColumn(final String data) {
        this(true);
        this.validate(data);
        if (null == data) {
            this.setData(null);
            this.setByteSize(0);
        } else {
            this.setData(Boolean.valueOf(data));
            this.setByteSize(1);
        }
    }

    public BoolColumn() {
        super(Type.BOOL, 1, null);
    }

    public Long asLong() {
        if (null == this.getData()) {
            return null;
        }

        return this.asBoolean() ? 1L : 0L;
    }

    public Double asDouble() {
        if (null == this.getData()) {
            return null;
        }

        return this.asBoolean() ? 1.0d : 0.0d;
    }

    public String asString() {

        if (null == super.getData()) {
            return null;
        }

        return this.asBoolean() ? "true" : "false";
    }

    public Date asDate() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bool类型不能转为Date .");
    }

    public byte[] asBytes() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Boolean类型不能转为Bytes .");
    }

    public Boolean asBoolean() {
        if (null == super.getData()) {
            return null;
        }

        return (Boolean) super.getData();
    }

    public BigDecimal asBigDecimal() {
        if (null == this.getData()) {
            return null;
        }

        return BigDecimal.valueOf(this.asLong());
    }

    public BigInteger asBigInteger() {
        if (null == this.getData()) {
            return null;
        }

        return BigInteger.valueOf(this.asLong());
    }

    private void validate(final String data) {
        if (null == data) {
            return;
        }

        if ("true".equalsIgnoreCase(data) || "false".equalsIgnoreCase(data)) {
            return;
        }

        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT,
                String.format("String[%s]不能转为Bool .", data));
    }
}
