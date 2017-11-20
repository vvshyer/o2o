package com.sun.o2o.web.frontend;

import com.sun.o2o.dto.AwardExecution;
import com.sun.o2o.entity.Award;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.UserShopMap;
import com.sun.o2o.service.AwardService;
import com.sun.o2o.service.UserShopMapService;
import com.sun.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class ShopAwardController {
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserShopMapService userShopMapService;

    @RequestMapping(value = "/listawardsbyshop",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listAwardByShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        //获取分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //获取当前的店铺信息
        long shopId = HttpServletRequestUtil.getLong(request,"shopId");
        //空值校验，主要确保shopId不为空
        if ((pageIndex > -1) && (pageSize > -1) && shopId > -1){
            String awardName = HttpServletRequestUtil.getString(request,"awardName");
            Award awardCondition = compactAwardCondition4Search(shopId,awardName);
            AwardExecution ae = awardService.getAwardList(awardCondition,pageIndex,pageSize);
            modelMap.put("awardList" ,ae.getAwardList());
            modelMap.put("count",ae.getCount());
            modelMap.put("success",true);
            //从Session中获取用户信息
            PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
            System.out.println("user" + user.getName());
            //空值判断
            if (user!=null && user.getUserId()!=null){
                //获取该用户在本店铺的积分信息
                UserShopMap userShopMap = userShopMapService.getUserShopMap(user.getUserId(),shopId);
                if (userShopMap == null){
                    modelMap.put("totalPoint",0);
                }else {
                    modelMap.put("totalPoint",userShopMap.getPoint());
                }
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    private Award compactAwardCondition4Search(long shopId, String awardName) {
        Award awardCondition = new Award();
        if (shopId!=-1L){
            awardCondition.setShopId(shopId);
        }
        if (awardName != null) {
            // 查询名字里包含shopName的店铺列表
            awardCondition.setAwardName(awardName);
        }
        // 前端展示的店铺都是审核成功的店铺
        awardCondition.setEnableStatus(1);
        return awardCondition;
    }
}
