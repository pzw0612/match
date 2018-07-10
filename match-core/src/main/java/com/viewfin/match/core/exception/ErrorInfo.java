package com.viewfin.match.core.exception;


import com.viewfin.match.core.enums.StatusCodeEnum;

public class ErrorInfo {
    private String msg;
    private int code;

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return code >= 200 && code<300 ? true : false;
    }

    public boolean isFailed() {
        return !isSuccess();
    }

    public void init() {
        this.code = StatusCodeEnum.SUCCESS.getCode();
        this.msg = StatusCodeEnum.SUCCESS.getMsg();
    }
}