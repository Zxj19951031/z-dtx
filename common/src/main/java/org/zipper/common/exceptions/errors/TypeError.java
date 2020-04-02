package org.zipper.common.exceptions.errors;

public enum TypeError implements IErrorCode {


    UNKNOWN_TYPE
            (70001, "不支持的类型"),
    ;

    private int code;
    private String description;

    TypeError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Integer getKey() {
        return this.code;
    }

    @Override
    public String getValue() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("Code:[%s], Describe:[%s]", this.code, this.description);
    }

}
