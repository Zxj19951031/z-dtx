package org.zipper.security.constant;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;
import org.zipper.security.constant.errors.IErrorCode;

/**
 * @author zhuxj
 * @since 2020/11/3
 */
public class SystemResponse<T> {
    private int code;
    private String message;
    private T data;

    private SystemResponse() {
        this.code = HttpStatus.OK.value();
        this.message = "success";
    }

    private SystemResponse(T data) {
        this.code = HttpStatus.OK.value();
        this.message = "success";
        this.data = data;

    }

    private SystemResponse(IErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    private SystemResponse(SystemException e) {
        this.code = e.getErrorCode().getCode();
        this.message = e.getMessage();
    }

    /**
     * 无参数成功
     *
     * @param <T> 泛型
     * @return org.springframework.http.ResponseEntity
     */
    public static <T> SystemResponse<T> success() {
        return new SystemResponse<>();
    }

    /**
     * 带有参数的成功返回
     *
     * @param data 参数
     * @param <T>  参数泛型
     * @return org.springframework.http.ResponseEntity
     */
    public static <T> SystemResponse<T> success(T data) {
        return new SystemResponse<>(data);
    }

    /**
     * 带有错误码的异常返回
     *
     * @param errorCode 错误码
     * @param <T>       泛型
     * @return org.springframework.http.ResponseEntity
     */
    public static <T> SystemResponse<T> error(IErrorCode errorCode) {
        return new SystemResponse<>(errorCode);
    }

    /**
     * 带有异常的返回
     *
     * @param exception SystemException
     * @param <T>       泛型
     * @return org.springframework.http.ResponseEntity
     */
    public static <T> SystemResponse<T> error(SystemException exception) {
        return new SystemResponse<>(exception);
    }

    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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
