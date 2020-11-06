package org.zipper.security.constant;


import org.zipper.security.constant.errors.IErrorCode;
import org.zipper.security.constant.errors.SystemError;

/**
 * @author zhuxj
 * @since 2020/11/3
 */
public class SystemException extends RuntimeException {

    private IErrorCode errorCode;

    private SystemException() {

    }

    private SystemException(IErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    private SystemException(IErrorCode errorCode, String extraMsg) {
        super(String.format("%s,%s", errorCode.getMsg(), extraMsg));
        this.errorCode = errorCode;
    }


    /**
     * 不待附加错误信息的运行时异常
     *
     * @param errorCode 错误码
     * @return SystemException
     */
    public static SystemException newException(IErrorCode errorCode) {
        return new SystemException(errorCode);
    }


    /**
     * 带有附加错误信息的运行时异常
     *
     * @param errorCode 错误码
     * @param extraMsg  附加信息
     * @return SystemException
     */
    public static SystemException newException(IErrorCode errorCode, String extraMsg) {
        return new SystemException(errorCode, extraMsg);
    }

    /**
     * 对Throwable的包装 带有自定义错误码
     *
     * @param errorCode 错误码
     * @param e         Throwable
     * @return SystemException
     */
    public static SystemException newException(IErrorCode errorCode, Throwable e) {
        return new SystemException(errorCode, e.getMessage());
    }

    /**
     * 对Throwable的包装
     *
     * @param e Throwable
     * @return SystemException
     */
    public static SystemException newException(Throwable e) {
        return new SystemException(SystemError.SERVER_ERROR, e.getMessage());
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }
}
