package com.sun.o2o.service.impl;

import com.sun.o2o.dao.AwardDao;
import com.sun.o2o.dto.AwardExecution;
import com.sun.o2o.dto.ImageHolder;
import com.sun.o2o.entity.Award;
import com.sun.o2o.enums.AwardStateEnum;
import com.sun.o2o.exceptions.AwardOperationException;
import com.sun.o2o.service.AwardService;
import com.sun.o2o.util.ImageUtil;
import com.sun.o2o.util.PageCalculator;
import com.sun.o2o.util.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AwardServiceImpl implements AwardService {

    @Autowired
    private AwardDao awardDao;

    @Override
    public AwardExecution getAwardList(Award awardCondition, int pageIndex, int pageSize) {
        //页转行
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex,pageSize);
        //根据查询条件分页取出奖品列表信息
        List<Award> awardList = awardDao.queryAwardList(awardCondition,rowIndex,pageSize);
        //根据相同的查询条件返回该查询条件下的奖品总数
        int count = awardDao.queryAwardCount(awardCondition);
        AwardExecution ae = new AwardExecution();
        ae.setAwardList(awardList);
        ae.setCount(count);
        return ae;
    }

    @Override
    public Award getAwardById(long awardId) {
        return awardDao.queryAwardByAwardId(awardId);
    }

    @Override
    @Transactional
    public AwardExecution addAward(Award award, ImageHolder thumbnail) {
        //空值判断
        if (award != null && award.getAwardId()!=null){
            //给award赋值
            award.setCreateTime(new Date());
            award.setLastEditTime(new Date());
            //award默认可用
            award.setEnableStatus(1);
            if (thumbnail!=null){
                addThumbnail(award,thumbnail);
            }
            try {
                int effectedNum = awardDao.insertAward(award);
                if (effectedNum <= 0){
                    throw new AwardOperationException("创建奖品失败");
                }
            }catch (Exception e){
                throw new AwardOperationException("创建奖品失败:"+e.getMessage());
            }
            return new AwardExecution(AwardStateEnum.SUCCESS,award);
        }else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }

    /**
     * 添加缩略图
     * @param award
     * @param thumbnail
     */
    private void addThumbnail(Award award, ImageHolder thumbnail) {
        String dest = PathUtil.getShopImagePath(award.getShopId());
        String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail,dest);
        award.setAwardImg(thumbnailAddr);
    }

    @Override
    @Transactional
    public AwardExecution modifyAward(Award award, ImageHolder thumbnail) {
        if (award!=null && award.getAwardId()!=null){
            award.setLastEditTime(new Date());
            if (thumbnail!=null){
                Award tempAward = awardDao.queryAwardByAwardId(award.getAwardId());
                if (tempAward.getAwardImg()!=null){
                    ImageUtil.deleteFileOrPath(tempAward.getAwardImg());
                }
                addThumbnail(award,thumbnail);
            }
            try {
                int effectedNum = awardDao.updateAward(award);
                if (effectedNum <= 0){
                    throw new AwardOperationException("更新奖品信息失败");
                }
                return new AwardExecution(AwardStateEnum.SUCCESS,award);
            }catch (Exception e){
                throw new AwardOperationException("更新奖品信息失败:"+e.getMessage());
            }
        }else {
            return new AwardExecution(AwardStateEnum.EMPTY);
        }
    }
}
