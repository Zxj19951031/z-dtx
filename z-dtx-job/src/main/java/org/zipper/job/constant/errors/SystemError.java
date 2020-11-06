package org.zipper.job.constant.errors;

/**
 * 系统内容异常
 *
 * @author zhuxj
 * @since 2020/10/13
 */
public enum SystemError implements IErrorCode {
    PARAMETER_ERROR(400, "参数错误"),
    NOT_LOGIN(401, "用户未登录"),
    AUTHENTICATE_ERROR(401, "认证失败"),
    USERNAME_PASSWORD_ERROR(401, "用户名或密码错误"),
    AUTHORIZE_ERROR(403, "鉴权失败，无权访问的资源"),
    SERVER_ERROR(500, "系统内部异常");

    private final int code;
    private final String description;

    SystemError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.description;
    }

    public String toString() {
        return String.format("Code:[%s], Describe:[%s]", this.code, this.description);
    }
}