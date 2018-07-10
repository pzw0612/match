package com.viewfin.match.core.entity;

import java.io.Serializable;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/6/29 09:54
 * @Description:
 */
public class OrderReq implements Serializable {

    private static final long serialVersionUID = 5061027614501302732L;

    private Long id;
    //订单id
    private Long orderId;
    //处理方法
    private String cmd;
    //请求报文
    private String requestData;
    //返回报文
    private String responseData;
    //当前状态
    private String status;
    //失败原因
    private String reason;

    //步骤
    private String process;
    //角色
    private String role;

    //url
    private String url;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getRequestData() {
        return requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
