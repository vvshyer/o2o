package com.sun.o2o.dao;

import com.sun.o2o.entity.Award;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.entity.UserAwardMap;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jws.soap.SOAPBinding;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAwardMapDaoTest {
    @Autowired
    private UserAwardMapDao userAwardMapDao;

    @Test
    public void testAInsertUserAwardMap() throws Exception{
        UserAwardMap userAwardMap = new UserAwardMap();
        PersonInfo customer = new PersonInfo();
        customer.setUserId(1L);
        userAwardMap.setUser(customer);
        userAwardMap.setOperator(customer);
        Award award = new Award();
        award.setAwardId(1L);
        userAwardMap.setAward(award);
        Shop shop = new Shop();
        shop.setShopId(1L);
        userAwardMap.setShop(shop);
        userAwardMap.setCreateTime(new Date());
        userAwardMap.setUsedStatus(1);
        userAwardMap.setPoint(1);
        int effectedNum = userAwardMapDao.insertUserAwardMap(userAwardMap);
        assertEquals(1, effectedNum);


        UserAwardMap userAwardMap2 = new UserAwardMap();
        PersonInfo customer2 = new PersonInfo();
        customer2.setUserId(1L);
        userAwardMap2.setUser(customer2);
        userAwardMap2.setOperator(customer2);
        Award award2 = new Award();
        award2.setAwardId(1L);
        userAwardMap2.setAward(award2);
        userAwardMap2.setShop(shop);
        userAwardMap2.setCreateTime(new Date());
        userAwardMap2.setUsedStatus(0);
        userAwardMap2.setPoint(1);
        effectedNum = userAwardMapDao.insertUserAwardMap(userAwardMap2);
        assertEquals(1, effectedNum);
    }

    @Test
    public void testBQueryUserAwardMapList() throws Exception{
        UserAwardMap userAwardMap = new UserAwardMap();
        List<UserAwardMap> userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap,0,3);
        assertEquals(3,userAwardMapList.size());
        int count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
        assertEquals(3,count);
//        PersonInfo customer = new PersonInfo();
//        //按用户名模糊查询
//        customer.setName("测试");
//        userAwardMap.setUser(customer);
//        userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap,0,3);
//        assertEquals(2,userAwardMapList.size());
//        count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
//        assertEquals(2,count);
//        //测试queryUserAwardMapById，预期按优先级排列返回第二个奖品的信息
//        userAwardMap = userAwardMapDao.queryUserAwardMapById(userAwardMapList.get(0).getUserAwardId());
//        assertEquals("我的奖品",userAwardMap.getAward().getAwardName());
    }

    @Test
    public void testQueryAllUserAwardMapList() throws Exception {
        UserAwardMap userAwardMap = new UserAwardMap();
        List<UserAwardMap> userAwardMapList = userAwardMapDao.queryReceivedUserAwardMapList(userAwardMap, 0, 999);
        for (UserAwardMap u:userAwardMapList){
            System.out.println(u.getOperator().getName());
        }
    }
    @Test
    public void testCUpdateUserAwardMap() throws Exception{
        UserAwardMap userAwardMap = new UserAwardMap();
        PersonInfo customer = new PersonInfo();
        customer.setName("测试");
        userAwardMap.setUser(customer);
        List<UserAwardMap> userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap,0,1);
        assertTrue("Error,积分不一致!",0==userAwardMapList.get(0).getUsedStatus());
        userAwardMapList.get(0).setUsedStatus(1);
        int effectedNum = userAwardMapDao.updateUserAwardMap(userAwardMapList.get(0));
        assertEquals(1,effectedNum);
    }
}
