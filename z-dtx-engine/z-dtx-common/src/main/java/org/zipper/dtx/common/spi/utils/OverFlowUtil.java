package org.zipper.dtx.common.spi.utils;

import org.zipper.common.exceptions.SysException;
import org.zipper.dtx.common.spi.errors.ColumnError;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OverFlowUtil {
    public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

    public static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

    public static final BigDecimal MIN_DOUBLE_POSITIVE = new BigDecimal(String.valueOf(Double.MIN_VALUE));

    public static final BigDecimal MAX_DOUBLE_POSITIVE = new BigDecimal(String.valueOf(Double.MAX_VALUE));

    private static boolean isLongOverflow(final BigInteger integer) {
        return (integer.compareTo(OverFlowUtil.MAX_LONG) > 0 || integer.compareTo(OverFlowUtil.MIN_LONG) < 0);
    }

    public static void validateLongNotOverFlow(BigInteger integer) {
        boolean isOverFlow = OverFlowUtil.isLongOverflow(integer);

        if (isOverFlow) {
            throw SysException.newException(
                    ColumnError.CONVERT_OVER_FLOW,
                    String.format("[%s] 转为Long类型出现溢出 .", integer.toString()));
        }
    }

    private static boolean isDoubleOverFlow(final BigDecimal decimal) {
        if (decimal.signum() == 0) {
            return false;
        }

        BigDecimal newDecimal = decimal;
        boolean isPositive = decimal.signum() == 1;
        if (!isPositive) {
            newDecimal = decimal.negate();
        }

        return (newDecimal.compareTo(MIN_DOUBLE_POSITIVE) < 0 || newDecimal.compareTo(MAX_DOUBLE_POSITIVE) > 0);
    }

    public static void validateDoubleNotOverFlow(final BigDecimal decimal) {
        boolean isOverFlow = OverFlowUtil.isDoubleOverFlow(decimal);
        if (isOverFlow) {
            throw SysException.newException(
                    ColumnError.CONVERT_OVER_FLOW,
                    String.format("[%s]转为Double类型出现溢出 .", decimal.toPlainString()));
        }
    }
}
