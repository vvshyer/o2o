package com.sun.o2o.enums;

public enum UserProductMapStateEnum {
    SUCCESS(1,"操作成功"),
    INNER_ERROR(-1001,"操作失败"),
    NULL_USER_PRODUCT_ID(-1002,"userProductId为空"),
    NULL_USER_PRODUCT_INFO(-1003,"传入了空的信息");

    private int state;

    private String stateInfo;

    private UserProductMapStateEnum(int state,String stateInfo){
        this.state = state;
        this.stateInfo =stateInfo;
    }

    /**
     * 依据传入的state返回相应的enum值
     */
    public static UserProductMapStateEnum stateOf(int state){
        for (UserProductMapStateEnum stateEnum:values()){
            if (stateEnum.getState() == state){
                return stateEnum;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }
}
