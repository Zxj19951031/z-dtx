package cn.com.citydo.dtx.common.spi.errors;

import cn.com.citydo.consts.enums.errors.IErrorCode;

public enum ColumnError implements IErrorCode {
    CONVERT_NOT_SUPPORT(1001, "类型转换错误"),
    CONVERT_OVER_FLOW(1002, "值溢出"),
    ARGUMENT_ERROR(1003, "参数错误");

    private int code;
    private String desc;

    ColumnError(int code, String desc) {
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
