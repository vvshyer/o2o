package com.sun.o2o.service.impl;

import com.sun.o2o.dao.ProductSellDailyDao;
import com.sun.o2o.entity.ProductSellDaily;
import com.sun.o2o.service.ProductSellDailyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {
    private static final Logger log = LoggerFactory.getLogger(ProductSellDailyServiceImpl.class);

    @Autowired
    private ProductSellDailyDao productSellDailyDao;

    @Override
    public void dailyCalculate() {
        log.info("Quartz Running!");
        productSellDailyDao.insertProductSellDaily();
        productSellDailyDao.insertDefaultProductSellDaily();
        log.info("Quartz finished!");
    }

    @Override
    public List<ProductSellDaily> listProductSellDaily(ProductSellDaily productSellDailyCondition, Date beginTime, Date endTime) {
        return productSellDailyDao.queryProductSellDailyList(productSellDailyCondition,beginTime,endTime);
    }
}
