package com.sun.o2o.service;

import com.sun.o2o.entity.Area;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaServiceTest{

    @Autowired
    private AreaService areaService;
    @Autowired
    private CacheService cacheService;

    @Test
    public void testGetAreaList(){
        List<Area> areaList = areaService.getAreaList();
        assertEquals(2,areaList.size());
        cacheService.removeFromCache(areaService.AREALISTKEY);
        areaList = areaService.getAreaList();
    }
}
