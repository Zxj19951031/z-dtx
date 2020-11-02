package org.zipper.plugins.common.errors;


import org.zipper.helper.exception.IErrorCode;

public enum ColumnError implements IErrorCode {
    CONVERT_NOT_SUPPORT(1001, "类型转换错误"),
    CONVERT_OVER_FLOW(1002, "值溢出"),
    ARGUMENT_ERROR(1003, "参数错误");

    private final int code;
    private final String msg;

    ColumnError(int code, String msg) {
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
