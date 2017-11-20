package com.sun.o2o.service;

import com.sun.o2o.dto.ShopAuthMapExecution;
import com.sun.o2o.entity.ShopAuthMap;
import com.sun.o2o.exceptions.ShopAuthMapOperationException;
import org.springframework.stereotype.Service;

public interface ShopAuthMapService {
    /**
     * 根据店铺id分页显示该店铺的授权信息
     * @param shopId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    ShopAuthMapExecution listShopAuthMapByShopId(Long shopId,Integer pageIndex,Integer pageSize);

    /**
     * 根据shopAuthId返回对应的授权信息
     * @param shopAuthId
     * @return
     */
    ShopAuthMap getShopAuthMapById(Long shopAuthId);

    /**
     * 添加授权信息
     * @param shopAuthMap
     * @return
     * @throws ShopAuthMapOperationException
     */
    ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap) throws ShopAuthMapOperationException;

    /**
     * 更新授权信息
     * @param shopAuthMap
     * @return
     * @throws ShopAuthMapOperationException
     */
    ShopAuthMapExecution modifyShopAuthMap(ShopAuthMap shopAuthMap) throws ShopAuthMapOperationException;

}
