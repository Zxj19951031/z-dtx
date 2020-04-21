package org.zipper.common.exceptions.errors;

public enum QuartzError implements IErrorCode {


    CRON_ERROR
            (80001, "调度表达式错误"),
    ;

    QuartzError(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    private Integer code;
    private String description;

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
