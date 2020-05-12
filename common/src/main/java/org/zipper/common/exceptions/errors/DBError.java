package org.zipper.common.exceptions.errors;

/**
 * 常见数据库异常
 */
public enum DBError implements IErrorCode {

    CONNECTION_FAILED
            (40001, "连接失败"),
    CONNECTION_TIMEOUT
            (40002, "连接超时"),
    INSERT_ERROR
            (40003, "新增记录失败"),
    LOGIN_ERROR
            (40004, "登录失败"),
    RECORD_NOT_EXIST
            (40005, "记录不存在"),
    CLOSE_ERROR
            (40006, "关闭连接失败"),
    QUERY_ERROR
            (40007, "查询失败");

    private int code;
    private String description;

    DBError(int code, String description) {
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
