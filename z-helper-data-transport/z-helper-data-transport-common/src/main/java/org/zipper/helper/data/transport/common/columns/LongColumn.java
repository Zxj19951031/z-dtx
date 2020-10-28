package org.zipper.helper.data.transport.common.columns;

import org.apache.commons.lang3.math.NumberUtils;
import org.zipper.helper.data.transport.common.errors.ColumnError;
import org.zipper.helper.data.transport.common.utils.OverFlowUtil;
import org.zipper.helper.exception.HelperException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class LongColumn extends Column {

    /**
     * 从整形字符串表示转为LongColumn，支持Java科学计数法
     * <p>
     * 如果data为浮点类型的字符串表示，数据将会失真，请使用DoubleColumn对接浮点字符串
     */
    public LongColumn(final String data) {
        super(Type.LONG, 0, null);
        if (null == data) {
            return;
        }

        try {
            BigInteger rawData = NumberUtils.createBigDecimal(data)
                    .toBigInteger();
            super.setData(rawData);

            // 当 rawData 为[0-127]时，rawData.bitLength() < 8，导致其 byteSize = 0，简单起见，直接认为其长度为 data.length()
            // super.setByteSize(rawData.bitLength() / 8);
            super.setByteSize(data.length());
        } catch (Exception e) {
            throw HelperException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[%s]不能转为Long .", data));
        }
    }

    public LongColumn(Long data) {
        this(null == data ? null : BigInteger.valueOf(data));
    }

    public LongColumn(Integer data) {
        this(null == data ? null : BigInteger.valueOf(data));
    }

    public LongColumn(BigInteger data) {
        this(data, null == data ? 0 : 8);
    }

    private LongColumn(BigInteger data, int byteSize) {
        super(Type.LONG, byteSize, data);
    }

    public LongColumn() {
        this((BigInteger) null);
    }


    @Override
    public Long asLong() {
        BigInteger rawData = (BigInteger) this.getData();
        if (null == rawData) {
            return null;
        }

        OverFlowUtil.validateLongNotOverFlow(rawData);

        return rawData.longValue();
    }

    @Override
    public Double asDouble() {

        if (null == this.getData()) {
            return null;
        }

        BigDecimal decimal = this.asBigDecimal();
        OverFlowUtil.validateDoubleNotOverFlow(decimal);

        return decimal.doubleValue();
    }

    @Override
    public String asString() {

        if (null == this.getData()) {
            return null;
        }
        return this.getData().toString();
    }

    @Override
    public Date asDate() {
        if (null == this.getData()) {
            return null;
        }
        return new Date(this.asLong());
    }

    @Override
    public byte[] asBytes() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Long类型不能转为Bytes .");
    }

    @Override
    public Boolean asBoolean() {
        if (null == this.getData()) {
            return null;
        }

        return this.asBigInteger().compareTo(BigInteger.ZERO) != 0;
    }

    @Override
    public BigDecimal asBigDecimal() {

        if (null == this.getData()) {
            return null;
        }

        return new BigDecimal(this.asBigInteger());
    }

    @Override
    public BigInteger asBigInteger() {

        if (null == this.getData()) {
            return null;
        }

        return (BigInteger) this.getData();
    }
}
