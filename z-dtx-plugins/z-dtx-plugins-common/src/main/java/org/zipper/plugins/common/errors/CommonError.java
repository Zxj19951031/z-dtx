package org.zipper.plugins.common.errors;

import org.zipper.helper.exception.IErrorCode;

public enum CommonError implements IErrorCode {

    SHUT_DOWN_TASK(2001, "Tunnel shutdown"),
    RUNTIME_ERROR(2002, "运行时异常"),
    CONFIG_ERROR(2003, "配置异常"),
    PLUGIN_INIT_ERROR(2004, "插件初始化异常"),
    PLUGIN_INSTALL_ERROR(2005, "插件装载异常"),
    PLUGIN_SPLIT_ERROR(2006, "插件分割异常");

    private final int code;
    private final String msg;

    CommonError(int code, String msg) {
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
