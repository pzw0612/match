package com.viewfin.match.core.enums;

public enum OrderTypeEnum implements IEnumSupport {
    limit("LIMIT","限价"),
    market("MARKET","市价");
    private String code;
    private String des;
    private OrderTypeEnum(String code , String des){
        this.code = code;
        this.des = des;
    }
    public static OrderTypeEnum getEnum(String code) {
        OrderTypeEnum arr[] = values();
        for(OrderTypeEnum tmp: arr) {
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
