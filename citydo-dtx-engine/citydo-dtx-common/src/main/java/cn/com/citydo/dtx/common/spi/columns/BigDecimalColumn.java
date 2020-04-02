package cn.com.citydo.dtx.common.spi.columns;


import cn.com.citydo.common.exceptions.SysException;
import cn.com.citydo.dtx.common.spi.errors.ColumnError;
import cn.com.citydo.dtx.common.spi.utils.OverFlowUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class BigDecimalColumn extends Column {
    public BigDecimalColumn(final String data) {
        this(data, null == data ? 0 : data.length());
        this.validate(data);
    }

    public BigDecimalColumn(Long data) {
        this(data == null ? (String) null : String.valueOf(data));
    }

    public BigDecimalColumn(Integer data) {
        this(data == null ? (String) null : String.valueOf(data));
    }

    /**
     * Double无法表示准确的小数数据，我们不推荐使用该方法保存Double数据，建议使用String作为构造入参
     */
    public BigDecimalColumn(final Double data) {
        this(data == null ? (String) null : new BigDecimal(String.valueOf(data)).toPlainString());
    }

    /**
     * Float无法表示准确的小数数据，我们不推荐使用该方法保存Float数据，建议使用String作为构造入参
     */
    public BigDecimalColumn(final Float data) {
        this(data == null ? (String) null : new BigDecimal(String.valueOf(data)).toPlainString());
    }

    public BigDecimalColumn(final BigDecimal data) {
        this(null == data ? (String) null : data.toPlainString());
    }

    public BigDecimalColumn(final BigInteger data) {
        this(null == data ? (String) null : data.toString());
    }

    public BigDecimalColumn() {
        this((String) null);
    }

    private BigDecimalColumn(final String data, int byteSize) {
        super(Type.BIGDecimal, byteSize, data);
    }


    public Long asLong() {
        if (null == this.getData()) {
            return null;
        }

        BigDecimal result = this.asBigDecimal();
        OverFlowUtil.validateLongNotOverFlow(result.toBigInteger());

        return result.longValue();
    }

    public Double asDouble() {
        if (null == this.getData()) {
            return null;
        }

        String string = (String) this.getData();

        boolean isDoubleSpecific = "NaN".equals(string) || "-Infinity".equals(string) || "+Infinity".equals(string);
        if (isDoubleSpecific) {
            return Double.valueOf(string);
        }

        BigDecimal result = this.asBigDecimal();
        OverFlowUtil.validateDoubleNotOverFlow(result);

        return result.doubleValue();
    }

    public String asString() {
        if (null == this.getData()) {
            return null;
        }
        return (String) this.getData();
    }

    public Date asDate() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "BigDecimal类型无法转为Date类型 .");
    }

    public byte[] asBytes() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "BigDecimal类型无法转为Bytes类型 .");
    }

    public Boolean asBoolean() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "BigDecimal类型无法转为Bool .");
    }

    public BigDecimal asBigDecimal() {
        if (null == this.getData()) {
            return null;
        }

        try {
            return new BigDecimal((String) this.getData());
        } catch (NumberFormatException e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[%s] 无法转换为BigDecimal类型 .", this.getData()));
        }
    }

    public BigInteger asBigInteger() {
        if (null == this.getData()) {
            return null;
        }

        return this.asBigDecimal().toBigInteger();
    }

    private void validate(final String data) {
        if (null == data) {
            return;
        }

        if ("NaN".equalsIgnoreCase(data) || "-Infinity".equalsIgnoreCase(data) || "Infinity".equalsIgnoreCase(data)) {
            return;
        }

        try {
            new BigDecimal(data);
        } catch (Exception e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[%s]无法转为BigDecimal类型 .", data));
        }
    }
}
