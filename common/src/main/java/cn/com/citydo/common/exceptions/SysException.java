package org.zipper.common.exceptions;


import org.zipper.common.exceptions.errors.IErrorCode;
import org.zipper.common.exceptions.errors.SystemError;

/**
 * @author zhuxj
 */
public class SysException extends RuntimeException {


    private IErrorCode error;

    private String msg;

    private SysException(IErrorCode error) {
        super(String.format("Code:[%s],Desc:[%s]", error.getCode(), error.getDescription()));
        this.error = error;
        this.msg = null;
    }

    private SysException(IErrorCode error, String msg) {
        super(String.format("Code:[%s],Desc:[%s],Msg:[%s]", error.getCode(), error.getDescription(), msg));
        this.error = error;
        this.msg = msg;
    }

    private SysException(IErrorCode error, Throwable e) {
        super(String.format("Code:[%s],Desc:[%s],Msg:[%s]", error.getCode(), error.getDescription(), e.getMessage()));
        this.error = error;
        this.msg = e.getMessage();
    }

    public static SysException newException(IErrorCode error) {
        return new SysException(error);
    }

    public static SysException newException(Throwable e) {
        return new SysException(SystemError.SYSTEM_ERROR, e);
    }

    public static SysException newException(IErrorCode error, String msg) {
        return new SysException(error, msg);
    }

    public static SysException newException(IErrorCode error, Throwable e) {
        return new SysException(error, e);
    }

    public IErrorCode getError() {
        return error;
    }

    public String getMsg() {
        return msg;
    }
}
