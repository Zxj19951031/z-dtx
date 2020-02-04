package cn.com.citydo.dtx.core.spi.columns;

import cn.com.citydo.dtx.core.errors.ColumnError;
import cn.com.citydo.dtx.core.utils.OverFlowUtil;
import cn.com.citydo.consts.exceptions.SysException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class DoubleColumn extends Column {

    public DoubleColumn(final String data) {
        this(data, null == data ? 0 : data.length());
        this.validate(data);
    }

    public DoubleColumn(Long data) {
        this(data == null ? (String) null : String.valueOf(data));
    }

    public DoubleColumn(Integer data) {
        this(data == null ? (String) null : String.valueOf(data));
    }

    /**
     * Double无法表示准确的小数数据，我们不推荐使用该方法保存Double数据，建议使用String作为构造入参
     */
    public DoubleColumn(final Double data) {
        this(data == null ? (String) null : new BigDecimal(String.valueOf(data)).toPlainString());
    }

    /**
     * Float无法表示准确的小数数据，我们不推荐使用该方法保存Float数据，建议使用String作为构造入参
     */
    public DoubleColumn(final Float data) {
        this(data == null ? (String) null : new BigDecimal(String.valueOf(data)).toPlainString());
    }

    public DoubleColumn(final BigDecimal data) {
        this(null == data ? (String) null : data.toPlainString());
    }

    public DoubleColumn(final BigInteger data) {
        this(null == data ? (String) null : data.toString());
    }

    public DoubleColumn() {
        this((String) null);
    }

    private DoubleColumn(final String data, int byteSize) {
        super(Type.DOUBLE, byteSize, data);
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
                ColumnError.CONVERT_NOT_SUPPORT, "Double类型无法转为Date类型 .");
    }

    public byte[] asBytes() {

        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Double类型无法转为Bytes类型 .");
    }

    public Boolean asBoolean() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Double类型无法转为Bool .");
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
                    String.format("String[%s] 无法转换为Double类型 .", (String) this.getData()));
        }
    }

    public BigInteger asBigInteger() {
        return null;
    }

    /**
     * 考虑到特殊类型的Double字符串
     *
     * @param data NaN、-Infinity、Infinity
     */
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
                    String.format("String[%s]无法转为Double类型 .", data));
        }
    }
}
