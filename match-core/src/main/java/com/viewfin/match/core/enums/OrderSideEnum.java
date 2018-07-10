package com.viewfin.match.core.enums;

/**
 * @Auther: pangzhiwang
 * @Date: 2018/7/5 23:31
 * @Description:
 */
public enum OrderSideEnum implements IEnumSupport {
    BID("BID","买"),
    ASK("ASK","卖"),
    CANCEL("CANCEL","取消");
    private String code;
    private String des;
    private OrderSideEnum(String code , String des){
        this.code = code;
        this.des = des;
    }
    public static OrderSideEnum getEnum(String code) {
        OrderSideEnum arr[] = values();
        for(OrderSideEnum tmp: arr) {
            if(tmp.code.equalsIgnoreCase(code)){
                return tmp;
            }
        }
        return null;
    }
    public String getCode() {
        return code;
    }
    public String getDes() {
        return des;
    }

    @Override
    public String toCode() {
        return code;
    }

    public String toString(){
        return  code;
    }
}