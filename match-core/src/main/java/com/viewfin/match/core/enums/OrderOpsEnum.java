package com.viewfin.match.core.enums;

public enum OrderOpsEnum implements IEnumSupport {
    BUY("BUY","买"),
    SELL("SELL","卖"),
    CANCEL("CANCEL","取消");
    private String code;
    private String des;
    private OrderOpsEnum(String code , String des){
        this.code = code;
        this.des = des;
    }
    public static OrderOpsEnum getEnum(String code) {
        OrderOpsEnum arr[] = values();
        for(OrderOpsEnum tmp: arr) {
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
