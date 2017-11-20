package com.sun.o2o.service;

import com.sun.o2o.dto.ImageHolder;
import com.sun.o2o.dto.ShopExecution;
import com.sun.o2o.entity.Area;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.entity.ShopCategory;
import com.sun.o2o.enums.ShopStateEnum;
import com.sun.o2o.exceptions.ShopOperationException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShopServiceTest{
    @Autowired
    private ShopService shopService;

    @Test
    public void testCGetShopList(){
        Shop shopCondition = new Shop();
        ShopCategory sc = new ShopCategory();
        sc.setShopCategoryId(1L);
        shopCondition.setShopCategory(sc);
        ShopExecution se = shopService.getShopList(shopCondition,1,2);
        System.out.println("店铺列表数："+se.getShopList().size());
        System.out.println("店铺总数："+se.getCount());
    }

    @Test
    public void testBModifyShop() throws ShopOperationException, FileNotFoundException {
        Shop shop = new Shop();
        shop.setShopId(1L);
        shop.setShopName("修改后");
        File shopImg = new File("/Users/sunhuayan/Pictures/tu1.png");
        InputStream is = new FileInputStream(shopImg);
        ImageHolder thumbnail = new ImageHolder("tu1.png",is);
        ShopExecution shopExecution = shopService.modifyShop(shop,thumbnail);
        System.out.println("新图片地址为："+shopExecution.getShop().getShopImg());
    }

    @Test
    public void testAAddShop() throws ShopOperationException, FileNotFoundException {
        Shop shop = new Shop();
        PersonInfo owner = new PersonInfo();
        Area area = new Area();
        ShopCategory shopCategory = new ShopCategory();
        owner.setUserId(1L);
        area.setAreaId(2);
        shopCategory.setShopCategoryId(10L);
        shop.setOwner(owner);
        shop.setArea(area);
        shop.setShopCategory(shopCategory);
        shop.setShopName("测试的店铺3");
        shop.setShopDesc("test3");
        shop.setShopAddr("test3");
        shop.setPhone("test3");
        shop.setCreateTime(new Date());
        shop.setEnableStatus(ShopStateEnum.CHECK.getState());
        shop.setAdvice("审核中");
        File shopImg = new File("/Users/sunhuayan/Pictures/pic.jpg");
        InputStream is = new FileInputStream(shopImg);
        ImageHolder thumbnail = new ImageHolder(shopImg.getName(),is);
        ShopExecution se = shopService.addShop(shop, thumbnail);
        assertEquals(ShopStateEnum.CHECK.getState(), se.getState());
    }
}
