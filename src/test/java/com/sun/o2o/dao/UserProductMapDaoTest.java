package com.sun.o2o.dao;

import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.Product;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.entity.UserProductMap;
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
public class UserProductMapDaoTest {
    @Autowired
    private UserProductMapDao userProductMapDao;

    @Test
    public void testAInsertUserProductMap() throws Exception{
        UserProductMap userProductMap = new UserProductMap();
        PersonInfo customer = new PersonInfo();
        customer.setUserId(1L);
        userProductMap.setUser(customer);
        userProductMap.setOperator(customer);
        Product product = new Product();
        product.setProductId(1L);
        userProductMap.setProduct(product);
        Shop shop = new Shop();
        shop.setShopId(29L);
        userProductMap.setShop(shop);
        userProductMap.setCreateTime(new Date());
        int effectedNum = userProductMapDao.insertUserProductMap(userProductMap);
        assertEquals(1,effectedNum);

        UserProductMap userProductMap2 = new UserProductMap();
        userProductMap2.setUser(customer);
        userProductMap2.setOperator(customer);
        Product product2 = new Product();
        product2.setProductId(1L);
        userProductMap2.setProduct(product2);
        Shop shop2 = new Shop();
        shop2.setShopId(29L);
        userProductMap2.setShop(shop2);
        userProductMap2.setCreateTime(new Date());
        effectedNum = userProductMapDao.insertUserProductMap(userProductMap2);
        assertEquals(1,effectedNum);

        UserProductMap userProductMap3 = new UserProductMap();
        userProductMap3.setUser(customer);
        userProductMap3.setOperator(customer);
        Product product3 = new Product();
        product3.setProductId(2L);
        userProductMap3.setProduct(product3);
        Shop shop3 = new Shop();
        shop3.setShopId(29L);
        userProductMap3.setShop(shop3);
        userProductMap3.setCreateTime(new Date());
        effectedNum = userProductMapDao.insertUserProductMap(userProductMap3);
        assertEquals(1,effectedNum);

        UserProductMap userProductMap4 = new UserProductMap();
        userProductMap4.setUser(customer);
        userProductMap4.setOperator(customer);
        Product product4 = new Product();
        product4.setProductId(16L);
        userProductMap4.setProduct(product4);
        Shop shop4 = new Shop();
        shop4.setShopId(28L);
        userProductMap4.setShop(shop4);
        userProductMap4.setCreateTime(new Date());
        effectedNum = userProductMapDao.insertUserProductMap(userProductMap4);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testBQueryUserProductMap() throws Exception{
        UserProductMap userProductMap = new UserProductMap();
        PersonInfo customer = new PersonInfo();
        //按顾客名字模糊查询
        customer.setName("测试");
        userProductMap.setUser(customer);
        List<UserProductMap> userProductMapList = userProductMapDao.queryUserProductMapList(userProductMap,0,2);
        assertEquals(2,userProductMapList.size());
        int count = userProductMapDao.queryUserProductMapCount(userProductMap);
        assertEquals(4,count);

        //叠加店铺去查询
        Shop shop = new Shop();
        shop.setShopId(29L);
        userProductMap.setShop(shop);
        userProductMapList = userProductMapDao.queryUserProductMapList(userProductMap,0,3);
        assertEquals(3,userProductMapList.size());
        count = userProductMapDao.queryUserProductMapCount(userProductMap);
        assertEquals(3,count);
    }
}
