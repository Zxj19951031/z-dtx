package org.zipper.transport.error;


import org.zipper.helper.exception.IErrorCode;

/**
 * jdbc 有关错误
 * @author zhuxj
 * @since 2020/08/28
 */
public enum JdbcError implements IErrorCode {
    UNSUPPORTED_DB_TYPE(2001,"不支持的数据源类型")
    ;

    private final Integer code;
    private final String msg;

    private JdbcError(Integer code, String msg) {
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
