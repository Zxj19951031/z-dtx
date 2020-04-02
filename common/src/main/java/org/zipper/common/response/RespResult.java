package org.zipper.common.response;

import org.zipper.common.exceptions.SysException;
import org.zipper.common.exceptions.errors.IErrorCode;
import com.alibaba.fastjson.JSON;

/**
 * 响应结果
 *
 * @author zhuxj
 */
public class RespResult {

    //状态码 非异常 200
    private Integer code;
    //状态值 非异常 success
    private String message;
    //返回值
    private Object data;

    private RespResult() {
    }

    private RespResult(Object data) {
        this.code = 200;
        this.message = "success";
        this.data = data;
    }

    private RespResult(IErrorCode error, Object data) {
        this.code = error.getCode();
        this.message = error.getDescription();
        this.data = data;
    }

    private RespResult(SysException e, Object data) {
        this.code = e.getError().getCode();
        this.message = e.getError().getDescription();
        this.data = data;
    }

    public static RespResult success(Object data) {
        return new RespResult(data);
    }

    public static RespResult error(SysException e) {
        return error(e, e.getMsg());
    }

    public static RespResult error(SysException e, Object data) {
        return new RespResult(e, data);
    }

    public static RespResult error(IErrorCode error) {
        return error(error, null);
    }

    public static RespResult error(IErrorCode error, Object data) {
        return new RespResult(error, data);
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
