package com.viewfin.match.core.enums;

/**
 *
 */
public enum TradeStatusEnum implements IEnumSupport {
    trade("trade","撮合中"),
    cancel("cancel","取消");
    private String code;
    private String des;
    private TradeStatusEnum(String code , String des){
        this.code = code;
        this.des = des;
    }
    public static TradeStatusEnum getEnum(String code) {
        TradeStatusEnum arr[] = values();
        for(TradeStatusEnum tmp: arr) {
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
