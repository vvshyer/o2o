package com.sun.o2o.dto;

import com.sun.o2o.entity.UserAwardMap;
import com.sun.o2o.entity.UserShopMap;
import com.sun.o2o.enums.UserAwardMapStateEnum;
import com.sun.o2o.enums.UserProductMapStateEnum;

import java.util.List;

public class UserAwardMapExecution {
    //结果状态
    private int state;

    //状态标识
    private String stateInfo;

    //授权数
    private Integer count;

    //操作的userAwardMap
    private UserAwardMap userAwardMap;

    //授权列表
    private List<UserAwardMap> userAwardMapList;

    public UserAwardMapExecution() {

    }

    public UserAwardMapExecution(UserAwardMapStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public UserAwardMapExecution(UserAwardMapStateEnum stateEnum, UserAwardMap userAwardMap) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.userAwardMap = userAwardMap;
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

    public UserAwardMap getUserAwardMap() {
        return userAwardMap;
    }

    public void setUserAwardMap(UserAwardMap userAwardMap) {
        this.userAwardMap = userAwardMap;
    }

    public List<UserAwardMap> getUserAwardMapList() {
        return userAwardMapList;
    }

    public void setUserAwardMapList(List<UserAwardMap> userAwardMapList) {
        this.userAwardMapList = userAwardMapList;
    }
}

