package org.zipper.transport.error;


import org.zipper.helper.exception.IErrorCode;

/**
 * 传输任务 有关错误
 * @author zhuxj
 * @since 2020/08/28
 */
public enum TransportError implements IErrorCode {
    REGISTER_ERROR(9001,"注册任务至调度中心失败")
    ;

    private final Integer code;
    private final String msg;

    private TransportError(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public String toString() {
        return String.format("Code=[%s],Message=[%s]", this.code, this.msg);
    }
}
