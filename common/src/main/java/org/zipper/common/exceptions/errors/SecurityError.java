package org.zipper.common.exceptions.errors;

/**
 * 常见安全异常
 */
public enum SecurityError implements IErrorCode {

    PERMISSION_DENIED
            (30001, "无权操作"),
    AUTHORIZATION_NO_PASS
            (30002, "授权不通过"),
    SESSION_TIMEOUT
            (30003, "会话已过期"),
    LOGIN_ERROR
            (30004, "登录失败"),
    ;

    private int code;
    private String description;

    SecurityError(int code, String description) {
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
    }}
