package cn.com.citydo.dtx.common.spi.errors;

import cn.com.citydo.consts.enums.errors.IErrorCode;

public enum CommonError implements IErrorCode {

    SHUT_DOWN_TASK(2001, "Tunnel shutdown");

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
