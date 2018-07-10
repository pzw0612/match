package com.viewfin.match.core.enums;

public enum SystemDoorEnum implements IEnumSupport {
    open("open","启动系统"),
    close("close","关掉系统");
    private String code;
    private String des;
    private SystemDoorEnum(String code , String des){
        this.code = code;
        this.des = des;
    }
    public static SystemDoorEnum getEnum(String code) {
        SystemDoorEnum arr[] = values();
        for(SystemDoorEnum tmp: arr) {
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
