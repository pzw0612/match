package com.viewfin.match.core.exception;



import com.viewfin.match.core.util.JSONUtil;

/**
 * Created by wdq.
 */

public class Result<T> {

    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T t) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(t);
        return result;
    }

    public static <T> Result<T> error() {
        return error(null);
    }

    public static <T> Result<T> error(String description) {
        return error(ErrorCode.error, description);
    }

    public static <T> Result<T> error(int code, String description) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(description);
        result.setData(null);
        return result;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
