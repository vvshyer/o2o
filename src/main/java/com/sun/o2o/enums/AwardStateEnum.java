package com.sun.o2o.enums;

public enum AwardStateEnum {
    OFFLINE(-1, "非法奖品"), DOWN(0, "下架"), SUCCESS(1, "操作成功"), INNER_ERROR(-1001, "操作失败"), EMPTY(-1002, "奖品为空");


    private int state;
    private String stateInfo;

    private AwardStateEnum(int state, String stateInfo){
        this.state = state;
        this.stateInfo =stateInfo;
    }

    /**
     * 依据传入的state返回相应的enum值
     */
    public static AwardStateEnum stateOf(int state){
        for (AwardStateEnum stateEnum:values()){
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
