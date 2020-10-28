package org.zipper.helper.data.transport.common.columns;

import org.zipper.helper.data.transport.common.errors.ColumnError;
import org.zipper.helper.exception.HelperException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class DateColumn extends Column {

    private DateType subType = DateType.DATETIME;

    public enum DateType {
        DATE, TIME, DATETIME
    }

    /**
     * 构建值为null的DateColumn，使用Date子类型为DATETIME
     */
    public DateColumn() {
        this((Long) null);
    }

    /**
     * 构建值为stamp(Unix时间戳)的DateColumn，使用Date子类型为DATETIME
     * 实际存储有date改为long的ms，节省存储
     */
    public DateColumn(final Long stamp) {
        super(Type.DATE, (null == stamp ? 0 : 8), stamp);
    }

    /**
     * 构建值为date(java.util.Date)的DateColumn，使用Date子类型为DATETIME
     */
    public DateColumn(final Date date) {
        this(date == null ? null : date.getTime());
    }

    /**
     * 构建值为date(java.sql.Date)的DateColumn，使用Date子类型为DATE，只有日期，没有时间
     */
    public DateColumn(final java.sql.Date date) {
        this(date == null ? null : date.getTime());
        this.setSubType(DateType.DATE);
    }

    /**
     * 构建值为time(java.sql.Time)的DateColumn，使用Date子类型为TIME，只有时间，没有日期
     */
    public DateColumn(final java.sql.Time time) {
        this(time == null ? null : time.getTime());
        this.setSubType(DateType.TIME);
    }

    /**
     * 构建值为ts(java.sql.Timestamp)的DateColumn，使用Date子类型为DATETIME
     */
    public DateColumn(final java.sql.Timestamp ts) {
        this(ts == null ? null : ts.getTime());
        this.setSubType(DateType.DATETIME);
    }

    @Override
    public Long asLong() {
        return (Long) this.getData();
    }

    @Override
    public Double asDouble() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Date类型不能转为Double .");
    }

    @Override
    public String asString() {
        try {
            return ColumnCast.date2String(this);
        } catch (Exception e) {
            throw HelperException.newException(
                    ColumnError.CONVERT_NOT_SUPPORT,
                    String.format("Date[%s]类型不能转为String .", this.toString()));
        }
    }

    @Override
    public Date asDate() {
        if (null == this.getData()) {
            return null;
        }

        return new Date((Long) this.getData());
    }

    @Override
    public byte[] asBytes() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Date类型不能转为Bytes .");
    }

    @Override
    public Boolean asBoolean() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Date类型不能转为Boolean .");
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Date类型不能转为BigDecimal .");
    }

    @Override
    public BigInteger asBigInteger() {
        throw HelperException.newException(
                ColumnError.CONVERT_NOT_SUPPORT, "Date类型不能转为BigInteger .");
    }

    public DateType getSubType() {
        return subType;
    }

    public void setSubType(DateType subType) {
        this.subType = subType;
    }
}
