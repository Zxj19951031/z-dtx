package org.zipper.common.exceptions.errors;

/**
 * 常见接口服务异常
 */
public enum ApiError implements IErrorCode {

    /**
     * 接口状态码异常
     */
    WRONG_STATUS
            (60001, "接口状态码异常");
    private Integer code;
    private String description;

    ApiError(Integer code, String description) {
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
