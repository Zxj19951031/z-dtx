package org.zipper.common.response;

import com.alibaba.fastjson.JSON;
import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.IErrorCode;

/**
 * 响应结果
 *
 * @author zhuxj
 */
public class RespResult<T> {

    //状态码 非异常 200
    private Integer code;
    //状态值 非异常 success
    private String message;
    //返回值
    private T data;

    private RespResult() {
    }

    private RespResult(T data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
    }

    private RespResult(IErrorCode error, T data) {
        this.code = error.getCode();
        this.message = error.getDescription();
        this.data = data;
    }

    private RespResult(SysException e, T data) {
        this.code = e.getError().getCode();
        this.message = e.getError().getDescription();
        this.data = data;
    }

    public static <T> RespResult<T> success(T data) {
        return new RespResult<>(data);
    }

    public static RespResult<String> error(SysException e) {
        return error(e, e.getMsg());
    }

    public static <T> RespResult<T> error(SysException e, T data) {
        return new RespResult<>(e, data);
    }

    public static <T> RespResult<T> error(IErrorCode error) {
        return error(error, null);
    }

    public static <T> RespResult<T> error(IErrorCode error, T data) {
        return new RespResult<>(error, data);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
