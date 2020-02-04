package cn.com.citydo.dtx.core.spi.columns;

import cn.com.citydo.dtx.core.errors.ColumnError;
import cn.com.citydo.consts.exceptions.SysException;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class BytesColumn extends Column {

    public BytesColumn() {
        this(null);
    }

    public BytesColumn(byte[] bytes) {
        super(Type.BYTES, null == bytes ? 0 : bytes.length, ArrayUtils.clone(bytes));
    }

    public Long asLong() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Long .");
    }

    public Double asDouble() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Double .");
    }

    public String asString() {
        if (null == this.getData()) {
            return null;
        }

        try {
            return ColumnCast.bytes2String(this);
        } catch (Exception e) {
            throw SysException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("Bytes[%s]不能转为String .", this.toString()));
        }
    }

    public Date asDate() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Date .");
    }

    public byte[] asBytes() {
        if (null == this.getData()) {
            return null;
        }

        return (byte[]) this.getData();
    }

    public Boolean asBoolean() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Boolean .");
    }

    public BigDecimal asBigDecimal() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bytes类型不能转为BigDecimal .");
    }

    public BigInteger asBigInteger() {
        throw SysException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Bytes类型不能转为BigInteger .");
    }
}
