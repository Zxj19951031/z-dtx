package cn.com.citydo.dtx.core.spi.columns;


import cn.com.citydo.dtx.core.errors.ColumnError;
import cn.com.citydo.dtx.core.utils.OverFlowUtil;
import cn.com.citydo.consts.exceptions.SysException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * String 类型列
 */
public class StringColumn extends Column {

    public StringColumn() {
        this(null);
    }

    public StringColumn(final String rawData) {
        super(Type.STRING, (null == rawData ? 0 : rawData.length()), rawData);
    }

    public Long asLong() {
        if (null == this.getData()) {
            return null;
        }

        this.validateDoubleSpecific((String) this.getData());

        try {
            BigInteger integer = this.asBigInteger();
            OverFlowUtil.validateLongNotOverFlow(integer);
            return integer.longValue();
        } catch (Exception e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[\"%s\"]不能转为Long .", this.asString()));
        }
    }

    public Double asDouble() {
        if (this.getData() == null)
            return null;

        String data = (String) this.getData();
        if ("NaN".equals(data)) {
            return Double.NaN;
        }

        if ("Infinity".equals(data)) {
            return Double.POSITIVE_INFINITY;
        }

        if ("-Infinity".equals(data)) {
            return Double.NEGATIVE_INFINITY;
        }

        return asBigDecimal().doubleValue();
    }

    public String asString() {
        if (null == this.getData()) {
            return null;
        }

        return (String) this.getData();
    }

    public Date asDate() {
        try {
            return ColumnCast.string2Date(this);
        } catch (Exception e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[\"%s\"]不能转为Date .", this.asString()));
        }
    }

    public byte[] asBytes() {
        try {
            return ColumnCast.string2Bytes(this);
        } catch (Exception e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[\"%s\"]不能转为Bytes .", this.asString()));
        }
    }

    public Boolean asBoolean() {
        if (null == this.getData()) {
            return null;
        }

        if ("true".equalsIgnoreCase(this.asString())) {
            return true;
        }

        if ("false".equalsIgnoreCase(this.asString())) {
            return false;
        }

        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT,
                String.format("String[\"%s\"]不能转为Bool .", this.asString()));
    }

    public BigDecimal asBigDecimal() {
        if (null == this.getData()) {
            return null;
        }

        this.validateDoubleSpecific((String) this.getData());

        try {
            return new BigDecimal(this.asString());
        } catch (Exception e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String [\"%s\"] 不能转为BigDecimal .", this.asString()));
        }
    }

    public BigInteger asBigInteger() {
        if (null == this.getData()) {
            return null;
        }

        this.validateDoubleSpecific((String) this.getData());

        try {
            return this.asBigDecimal().toBigInteger();
        } catch (Exception e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[\"%s\"]不能转为BigInteger .", this.asString()));
        }
    }


    /**
     * 特殊Double类型，不能转换
     *
     * @param data NaN、Infinity、-Infinity
     */
    private void validateDoubleSpecific(final String data) {
        if ("NaN".equals(data) || "Infinity".equals(data) || "-Infinity".equals(data)) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("String[\"%s\"]属于Double特殊类型，不能转为其他类型 .", data));
        }
    }

}
