package org.zipper.helper.data.transport.common.errors;

import org.zipper.helper.exception.IErrorCode;

public enum PluginError implements IErrorCode {
    TASK_INIT_ERROR(3001, "初始化异常"),
    TASK_UNSUPPORTED_TYPE(3002, "字段类型不支持"),
    TASK_READ_ERROR(3004, "读取异常"),
    GET_COLUMN_INFO_FAILED(3005, "获取列表元数据失败"),
    TASK_WRITE_ERROR(3006, "写入异常");

    private final int code;
    private final String msg;

    PluginError(Integer code, String msg) {
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
