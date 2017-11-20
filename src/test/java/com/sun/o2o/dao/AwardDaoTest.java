package com.sun.o2o.dao;

import com.sun.o2o.entity.Award;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AwardDaoTest {

    @Autowired
    private AwardDao awardDao;

    @Test
    public void testAInsertAward() throws Exception{
        long shopId = 1;
        Award award1 = new Award();
        award1.setAwardName("测试1");
        award1.setAwardImg("test1");
        award1.setPoint(5);
        award1.setPriority(1);
        award1.setEnableStatus(1);
        award1.setCreateTime(new Date());
        award1.setLastEditTime(new Date());
        award1.setShopId(shopId);
        int effectedNum = awardDao.insertAward(award1);
        assertEquals(1,effectedNum);

        Award award2 = new Award();
        award2.setAwardName("测试2");
        award2.setAwardImg("test2");
        award2.setPoint(2);
        award2.setPriority(2);
        award2.setEnableStatus(0);
        award2.setCreateTime(new Date());
        award2.setLastEditTime(new Date());
        award2.setShopId(shopId);
        effectedNum = awardDao.insertAward(award2);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testBQueryAwardList() throws Exception{
        Award award = new Award();
        List<Award> awardList = awardDao.queryAwardList(award,0,3);
        assertEquals(2,awardList.size());
        int count = awardDao.queryAwardCount(award);
        assertEquals(2,count);
        award.setAwardName("测试");
        awardList = awardDao.queryAwardList(award,0,3);
        assertEquals(2,awardList.size());
        count = awardDao.queryAwardCount(award);
        assertEquals(2,count);
    }

    @Test
    public void testCQueryAwardByAwardId() throws Exception{
        Award awardCondition = new Award();
        awardCondition.setAwardName("测试1");
        //按照特定名字查询
        List<Award> awardList = awardDao.queryAwardList(awardCondition,0,1);
        assertEquals(1,awardList.size());
        //通过特定名字查询返回的特定奖品的id去测试
        Award award = awardDao.queryAwardByAwardId(awardList.get(0).getAwardId());
        assertEquals("测试1",award.getAwardName());
    }

    @Test
    public void testDUpdateAward() throws Exception{
        Award awardCondition = new Award();
        awardCondition.setAwardName("测试1");
        //按照特定名字查询返回特定的商品
        List<Award> awardList = awardDao.queryAwardList(awardCondition,0,1);
        //修改该商品的名称
        awardList.get(0).setAwardName("第一个测试奖品");
        int effectedNum = awardDao.updateAward(awardList.get(0));
        assertEquals(1,effectedNum);
        //将修改名称后的奖品找出来并验证
        Award award = awardDao.queryAwardByAwardId(awardList.get(0).getAwardId());
        assertEquals("第一个测试奖品",award.getAwardName());
    }

    @Test
    public void testEDeleteAward() throws Exception{
        Award awardCondition = new Award();
        awardCondition.setAwardName("测试");
        List<Award> awardList = awardDao.queryAwardList(awardCondition,0,2);
        assertEquals(2,awardList.size());
        for (Award award:awardList){
            int effectedNum = awardDao.deleteAward(award.getAwardId(),award.getShopId());
            assertEquals(1,effectedNum);
        }
    }
}
