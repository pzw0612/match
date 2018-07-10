package com.viewfin.match.core.enums;

public enum StatusCodeEnum {
    SUCCESS(200,"成功"),
    FAILED(-1,"普通通用异常"),
    bad_request(400,"bad_request"),
    unauthorized(401,"unauthorized"),
    payment_required(402,"payment_required"),
    forbidden(403,"forbidden"),
    not_found(404,"not_found"),
    method_not_allowed(405,"method_not_allowed"),
    length_required(411,"length_required"),
    internal_server_error(500,"internal_server_error"),
    not_implemented(501,"not_implemented"),
    bad_gateway(502,"bad_gateway"),
    service_unavailable(503,"service_unavailable"),
    gateway_timeout(504,"gateway_timeout"),
	JSON_PARSE_FAILED(-101,"json解析失败"),
    SEND_EMAIL_FAILED(-111,"email发送失败"),
    HTTP_RESPONSE_SUCCESS(200,"http链接成功"),
    HTTP_RESPONSE_FALID(-200,"http链接失败"),
    MATCH_CREATEORDER_FALID(600,"撮合创建订单异常"),
    MATCH_CANCElORDER_FALID(601,"撮合撤销订单异常");


    private int	code;
    private String msg;

    private StatusCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
}
