package cn.com.citydo.dtx.common.spi.errors;

import cn.com.citydo.consts.enums.errors.IErrorCode;

public enum PluginError implements IErrorCode {
    TASK_INIT_ERROR(3001, "初始化异常"),
    TASK_UNSUPPORTED_TYPE(3002, "字段类型不支持"),
    TASK_READ_ERROR(3004, "读取异常"),
    GET_COLUMN_INFO_FAILED(3005, "获取列表元数据失败"),
    TASK_WRITE_ERROR(3006, "写入异常");

    private Integer key;
    private String value;

    PluginError(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Integer getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
