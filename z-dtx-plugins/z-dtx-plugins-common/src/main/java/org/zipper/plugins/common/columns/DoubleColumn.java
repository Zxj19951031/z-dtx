package org.zipper.plugins.common.columns;

import org.zipper.plugins.common.errors.ColumnError;
import org.zipper.plugins.common.utils.OverFlowUtil;
import org.zipper.helper.exception.HelperException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class DoubleColumn extends Column {

    public DoubleColumn(final String data) {
        this(data, null == data ? 0 : data.length());
        this.validate(data);
    }

    public DoubleColumn(Long data) {
        this(data == null ? null : String.valueOf(data));
    }

    public DoubleColumn(Integer data) {
        this(data == null ? null : String.valueOf(data));
    }

    /**
     * Double无法表示准确的小数数据，我们不推荐使用该方法保存Double数据，建议使用String作为构造入参
     */
    public DoubleColumn(final Double data) {
        this(data == null ? null : new BigDecimal(String.valueOf(data)).toPlainString());
    }

    /**
     * Float无法表示准确的小数数据，我们不推荐使用该方法保存Float数据，建议使用String作为构造入参
     */
    public DoubleColumn(final Float data) {
        this(data == null ? null : new BigDecimal(String.valueOf(data)).toPlainString());
    }

    public DoubleColumn(final BigDecimal data) {
        this(null == data ? null : data.toPlainString());
    }

    public DoubleColumn(final BigInteger data) {
        this(null == data ? null : data.toString());
    }

    public DoubleColumn() {
        this((String) null);
    }

    private DoubleColumn(final String data, int byteSize) {
        super(Type.DOUBLE, byteSize, data);
    }


    @Override
    public Long asLong() {

        if (null == this.getData()) {
            return null;
        }

        BigDecimal result = this.asBigDecimal();
        OverFlowUtil.validateLongNotOverFlow(result.toBigInteger());

        return result.longValue();
    }

    @Override
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

    @Override
    public String asString() {

        if (null == this.getData()) {
            return null;
        }

        return (String) this.getData();
    }

    @Override
    public Date asDate() {

        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Double类型无法转为Date类型 .");
    }

    @Override
    public byte[] asBytes() {

        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Double类型无法转为Bytes类型 .");
    }

    @Override
    public Boolean asBoolean() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Double类型无法转为Bool .");
    }

    @Override
    public BigDecimal asBigDecimal() {
        if (null == this.getData()) {
            return null;
        }

        try {
            return new BigDecimal((String) this.getData());
        } catch (NumberFormatException e) {
            throw HelperException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[%s] 无法转换为Double类型 .", this.getData()));
        }
    }

    @Override
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
            throw HelperException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[%s]无法转为Double类型 .", data));
        }
    }
}
