package com.sun.o2o.dao;

import com.sun.o2o.entity.ProductSellDaily;
import com.sun.o2o.entity.Shop;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductSellDailyDaoTest {
    @Autowired
    private ProductSellDailyDao productSellDailyDao;

    @Test
    public void testAInsertProductSellDaily() throws Exception{
        //创建商品日销量统计
        int effectedNum = productSellDailyDao.insertProductSellDaily();
        assertEquals(1,effectedNum);
    }

    @Test
    public void testBInsertProductSellDaily() throws Exception{
        //创建商品日销量统计
        int effectedNum = productSellDailyDao.insertDefaultProductSellDaily();
        assertEquals(12,effectedNum);
    }

    @Test
    public void testCQueryProductSellDaily() throws Exception{
        ProductSellDaily productSellDaily = new ProductSellDaily();
        //叠加店铺去查询
        Shop shop = new Shop();
        shop.setShopId(28L);
        productSellDaily.setShop(shop);
        List<ProductSellDaily> productSellDailyList = productSellDailyDao.queryProductSellDailyList(productSellDaily,null,null);
        for (ProductSellDaily p : productSellDailyList){
            System.out.println(p.getCreateTime());
        }
    }
}
