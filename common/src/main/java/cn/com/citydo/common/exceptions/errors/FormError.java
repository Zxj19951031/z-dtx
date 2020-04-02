package cn.com.citydo.common.exceptions.errors;

/**
 * 常见表单类异常
 */
public enum FormError implements IErrorCode {

    PHONE_FORMAT_ERROR
            (20001, "手机号格式不正确"),
    PHONE_REGISTRY_ERROR
            (20002, "手机号已被注册"),
    TELEPHONE_FORMAT_ERROR
            (20003, "座机号格式不正确"),
    TELEPHONE_REGISTRY_ERROR
            (20004, "座机号已被注册"),
    MAIL_FORMAT_ERROR
            (20005, "邮箱格式不正确"),
    MAIL_REGISTRY_ERROR
            (20006, "邮箱已被注册"),
    PARAMETER_TOO_LONG_ERROR
            (20007, "输入值过长"),
    PARAMETER_TOO_SHORT_ERROR
            (20008, "输入值过短"),
    SPECIAL_CHAR_ERROR
            (20009, "存在特殊字符"),
    VERIFICATION_ERROR
            (20010, "验证码错误"),
    USERNAME_REGISTRY_ERROR
            (20011, "用户名已被注册"),
    ROLE_EXIST_ERROR
            (20012, "该角色已存在"),
    ;

    private int code;
    private String description;

    FormError(int code, String description) {
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
