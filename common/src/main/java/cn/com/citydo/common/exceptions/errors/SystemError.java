package cn.com.citydo.common.exceptions.errors;

/**
 * 常见系统内部异常
 */
public enum SystemError implements IErrorCode {

    SYSTEM_ERROR
            (10001, "系统内部错误"),
    INDEX_ERROR
            (10002, "创建索引库异常"),
    IO_ERROR
            (10003, "IO 异常");

    private int code;
    private String description;

    SystemError(int code, String description) {
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
