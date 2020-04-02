package org.zipper.dtx.common.spi.columns;

import org.zipper.common.exceptions.SysException;
import org.zipper.dtx.common.spi.errors.ColumnError;
import org.zipper.dtx.common.spi.utils.OverFlowUtil;
import org.apache.commons.lang3.math.NumberUtils;

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
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[%s]不能转为Long .", data));
        }
    }

    public LongColumn(Long data) {
        this(null == data ? (BigInteger) null : BigInteger.valueOf(data));
    }

    public LongColumn(Integer data) {
        this(null == data ? (BigInteger) null : BigInteger.valueOf(data));
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


    public Long asLong() {
        BigInteger rawData = (BigInteger) this.getData();
        if (null == rawData) {
            return null;
        }

        OverFlowUtil.validateLongNotOverFlow(rawData);

        return rawData.longValue();
    }

    public Double asDouble() {

        if (null == this.getData()) {
            return null;
        }

        BigDecimal decimal = this.asBigDecimal();
        OverFlowUtil.validateDoubleNotOverFlow(decimal);

        return decimal.doubleValue();
    }

    public String asString() {

        if (null == this.getData()) {
            return null;
        }
        return ((BigInteger) this.getData()).toString();
    }

    public Date asDate() {
        if (null == this.getData()) {
            return null;
        }
        return new Date(this.asLong());
    }

    public byte[] asBytes() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Long类型不能转为Bytes .");
    }

    public Boolean asBoolean() {
        if (null == this.getData()) {
            return null;
        }

        return this.asBigInteger().compareTo(BigInteger.ZERO) != 0;
    }

    public BigDecimal asBigDecimal() {

        if (null == this.getData()) {
            return null;
        }

        return new BigDecimal(this.asBigInteger());
    }

    public BigInteger asBigInteger() {

        if (null == this.getData()) {
            return null;
        }

        return (BigInteger) this.getData();
    }
}
