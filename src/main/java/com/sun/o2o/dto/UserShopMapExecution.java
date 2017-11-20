package com.sun.o2o.dto;

import com.sun.o2o.entity.UserProductMap;
import com.sun.o2o.entity.UserShopMap;
import com.sun.o2o.enums.UserProductMapStateEnum;

import java.util.List;

public class UserShopMapExecution {
    //结果状态
    private int state;

    //状态标识
    private String stateInfo;

    //授权数
    private Integer count;

    //操作的userShopMap
    private UserShopMap userShopMap;

    //授权列表
    private List<UserShopMap> userShopMapList;

    public UserShopMapExecution(){

    }

    public UserShopMapExecution(UserProductMapStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public UserShopMapExecution(UserProductMapStateEnum stateEnum, UserShopMap userShopMap) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.userShopMap = userShopMap;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public UserShopMap getUserShopMap() {
        return userShopMap;
    }

    public void setUserShopMap(UserShopMap userShopMap) {
        this.userShopMap = userShopMap;
    }

    public List<UserShopMap> getUserShopMapList() {
        return userShopMapList;
    }

    public void setUserShopMapList(List<UserShopMap> userShopMapList) {
        this.userShopMapList = userShopMapList;
    }
}
