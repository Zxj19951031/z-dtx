package org.zipper.common.exceptions.errors;

public enum JsonError implements IErrorCode {


    JSON_PARES_ERROR
            (50001, "从JSON串加载JSON对象异常，请检查JSON串格式"),
    CONFIG_ERROR
            (50002, "JSON配置异常");

    JsonError(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    private Integer code;
    private String description;

    @Override
    public Integer getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Describe:[%s]", this.code, this.description);
    }


}
