package com.sun.o2o.enums;

public enum UserAwardMapStateEnum {
    SUCCESS(1,"操作成功"),
    INNER_ERROR(-1001,"操作失败"),
    NULL_USER_AWARD_ID(-1002,"userAwarId为空"),
    NULL_USER_AWARD_INFO(-1003,"传入了空的信息");

    private int state;

    private String stateInfo;

    private UserAwardMapStateEnum(int state, String stateInfo){
        this.state = state;
        this.stateInfo =stateInfo;
    }

    /**
     * 依据传入的state返回相应的enum值
     */
    public static UserAwardMapStateEnum stateOf(int state){
        for (UserAwardMapStateEnum stateEnum:values()){
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
