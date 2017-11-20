package com.sun.o2o.service;

import com.sun.o2o.dto.UserProductMapExecution;
import com.sun.o2o.dto.UserShopMapExecution;
import com.sun.o2o.entity.UserProductMap;
import com.sun.o2o.entity.UserShopMap;

public interface UserShopMapService {

    /**
     * 通过传入的查询条件分页列出用户积分列表
     * @param userShopMapCondition
     * @param pageIndex
     * @param pageSize
     * @return
     */
    UserShopMapExecution listUserShopMap(UserShopMap userShopMapCondition, Integer pageIndex, Integer pageSize);


    /**
     * 根据用户Id和店铺Id返回该用户在某个店铺的积分情况
     * @param userId
     * @param shopId
     * @return
     */
    UserShopMap getUserShopMap(long userId,long shopId);
}
