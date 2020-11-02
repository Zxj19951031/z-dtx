package org.zipper.plugins.common.columns;

import org.zipper.plugins.common.errors.ColumnError;
import org.zipper.helper.exception.HelperException;

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

    @Override
    public Long asLong() {
        if (null == this.getData()) {
            return null;
        }

        return this.asBoolean() ? 1L : 0L;
    }

    @Override
    public Double asDouble() {
        if (null == this.getData()) {
            return null;
        }

        return this.asBoolean() ? 1.0d : 0.0d;
    }

    @Override
    public String asString() {

        if (null == super.getData()) {
            return null;
        }

        return this.asBoolean() ? "true" : "false";
    }

    @Override
    public Date asDate() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bool类型不能转为Date .");
    }

    @Override
    public byte[] asBytes() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Boolean类型不能转为Bytes .");
    }

    @Override
    public Boolean asBoolean() {
        if (null == super.getData()) {
            return null;
        }

        return (Boolean) super.getData();
    }

    @Override
    public BigDecimal asBigDecimal() {
        if (null == this.getData()) {
            return null;
        }

        return BigDecimal.valueOf(this.asLong());
    }

    @Override
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

        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT,
                String.format("String[%s]不能转为Bool .", data));
    }
}
