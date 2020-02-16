package cn.com.citydo.dtx.common.spi.errors;

import cn.com.citydo.consts.enums.errors.IErrorCode;

public enum CommonError implements IErrorCode {

    SHUT_DOWN_TASK(2001, "Tunnel shutdown"),
    RUNTIME_ERROR(2002, "运行时异常"),
    CONFIG_ERROR(2003, "配置异常"),
    PLUGIN_INIT_ERROR(2004, "插件初始化异常"),
    PLUGIN_INSTALL_ERROR(2005, "插件装载异常"),
    PLUGIN_SPLIT_ERROR(2006, "插件分割异常");

    private int code;
    private String desc;

    CommonError(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public Integer getKey() {
        return this.code;
    }

    public String getValue() {
        return this.desc;
    }
}
