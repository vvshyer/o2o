package com.sun.o2o.dao;

import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.entity.UserShopMap;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserShopMapDaoTest {
    @Autowired
    private UserShopMapDao userShopMapDao;

    @Test
    public void testAInsertUserShopMap() throws Exception{
        UserShopMap userShopMap = new UserShopMap();
        PersonInfo customer = new PersonInfo();
        customer.setUserId(1L);
        userShopMap.setUser(customer);
        Shop shop = new Shop();
        shop.setShopId(29L);
        userShopMap.setShop(shop);
        userShopMap.setCreateTime(new Date());
        userShopMap.setPoint(1);
        int effectedNum = userShopMapDao.insertUserShopMap(userShopMap);
        assertEquals(1,effectedNum);

        UserShopMap userShopMap2 = new UserShopMap();
        PersonInfo customer2 = new PersonInfo();
        customer2.setUserId(20L);
        userShopMap2.setUser(customer2);
        Shop shop2 = new Shop();
        shop2.setShopId(28L);
        userShopMap2.setShop(shop2);
        userShopMap2.setCreateTime(new Date());
        userShopMap2.setPoint(1);
        effectedNum = userShopMapDao.insertUserShopMap(userShopMap2);

        assertEquals(1,effectedNum);
    }

    @Test
    public void testBQueryUserShopMap() throws Exception{
        UserShopMap userShopMap = new UserShopMap();
        //查全部
        List<UserShopMap> userProductMapList = userShopMapDao.queryUserShopMapList(userShopMap,0,3);
        assertEquals(2,userProductMapList.size());
        int count = userShopMapDao.queryUserShopMapCount(userShopMap);
        assertEquals(2,count);
//
//        //按店铺查询
//        Shop shop = new Shop();
//        shop.setShopId(29L);
//        userShopMap.setShop(shop);
//        userProductMapList = userShopMapDao.queryUserShopMapList(userShopMap,0,3);
//        assertEquals(1,userProductMapList.size());
//        count = userShopMapDao.queryUserShopMapCount(userShopMap);
//        assertEquals(1,count);
//
//        //按用户Id和店铺查询
//        userShopMap = userShopMapDao.queryUserShopMap(1,29);
//        assertEquals("测试",userShopMap.getUser().getName());
    }

    @Test
    public void testCUpdateUserShopMap() throws Exception{
        UserShopMap userShopMap = new UserShopMap();
        userShopMap = userShopMapDao.queryUserShopMap(1,29);
        assertTrue("Error,积分不一致！",1==userShopMap.getPoint());
        userShopMap.setPoint(2);
        int effectedNum = userShopMapDao.updateUserShopMapPoint(userShopMap);
        assertEquals(1,effectedNum);
    }
}
