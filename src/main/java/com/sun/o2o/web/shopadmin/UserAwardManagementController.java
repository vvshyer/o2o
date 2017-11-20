package com.sun.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.o2o.dto.ShopAuthMapExecution;
import com.sun.o2o.dto.UserAwardMapExecution;
import com.sun.o2o.dto.WechatInfo;
import com.sun.o2o.entity.*;
import com.sun.o2o.enums.UserAwardMapStateEnum;
import com.sun.o2o.service.PersonInfoService;
import com.sun.o2o.service.ShopAuthMapService;
import com.sun.o2o.service.UserAwardMapService;
import com.sun.o2o.service.WechatAuthService;
import com.sun.o2o.util.CodeUtil;
import com.sun.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class UserAwardManagementController {
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private PersonInfoService personInfoService;
    @Autowired
    private ShopAuthMapService shopAuthMapService;

    @RequestMapping(value = "/listuserawardmapsbyshop",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listUserAwardMapsByShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        //获取分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //获取当前的店铺信息
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        //空值校验，主要确保shopId不为空
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop!=null) && (currentShop.getShopId()!=null)){
            //添加查询条件
            UserAwardMap userAwardMap = new UserAwardMap();
            userAwardMap.setShop(currentShop);
            String awardName = HttpServletRequestUtil.getString(request,"awardName");
            if (awardName!=null){
                //若前端想按照商品名模糊查询，则传入awardName
                Award award = new Award();
                award.setAwardName(awardName);
                userAwardMap.setAward(award);
            }
            //根据传入的查询数据获取该店铺的商品销售情况
            UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMap,pageIndex,pageSize);
            modelMap.put("userAwardMapList" ,ue.getUserAwardMapList());
            modelMap.put("count",ue.getCount());
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/exchangeaward",method = RequestMethod.GET)
    private String exchangeAward(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //获取负责扫描二维码的店员信息
        WechatAuth auth = CodeUtil.getOperatorInfo(request,wechatAuthService);
        if (auth != null){
            //通过userId获取店员信息
            PersonInfo operator = personInfoService.getPersonInfoById(auth.getPersonInfo().getUserId());
            //设置用户的session
            request.getSession().setAttribute("user",operator);
            //解析微信回传过来的自定义参数state
            String qrCodeinfo = new String(
                    URLDecoder.decode(HttpServletRequestUtil.getString(request,"state"),"UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            WechatInfo wechatInfo = null;
            try {
                //将解码后的内容用aaa替换
                wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa","\""),WechatInfo.class);
            }catch (Exception e){
                return "shop/operationfail";
            }
            //校验二维码是否已经过期
            if (!CodeUtil.checkQRCodeInfo(wechatInfo)){
                return "shop/operationfail";
            }
            //获取用户奖品映射主键
            Long userAwardId = wechatInfo.getUserAwardId();
            //获取顾客Id
            Long customerId = wechatInfo.getCustomerId();
            //将顾客信息，操作员信息以及奖品信息封装
            UserAwardMap userAwardMap = compactUserAwardMap4Exchange(customerId,userAwardId,operator);
            if (userAwardMap != null){
                try {
                    //检查该员工是否具有扫码权限
                    if (!checkShopAuth(operator.getUserId(),userAwardMap)){
                        return "shop/operationfail";
                    }
                    //修改奖品的领取状态
                    UserAwardMapExecution se = userAwardMapService.modifyUserAwardMap(userAwardMap);
                    if (se.getState() == UserAwardMapStateEnum.SUCCESS.getState()){
                        return "shop/operationsuccess";
                    }
                }catch (RuntimeException e){
                    return "shop/operationfail";
                }
            }
        }
        return "shop/operationfail";
    }

    /**
     * 检查扫码的人员是否有操作权限
     * @param userId
     * @param userAwardMap
     * @return
     */
    private boolean checkShopAuth(Long userId, UserAwardMap userAwardMap) {
        //获取该店铺的所有授权信息
        ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService
                .listShopAuthMapByShopId(userAwardMap.getShop().getShopId(),1,1000);
        for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()){
            //看看是否给过该人员进行授权
            if (shopAuthMap.getEmployee().getUserId() == userId){
                return true;
            }
        }
        return false;
    }

    private UserAwardMap compactUserAwardMap4Exchange(Long customerId, Long userAwardId, PersonInfo operator) {
        UserAwardMap userAwardMap = new UserAwardMap();
        if (customerId>-1L){
            PersonInfo user = new PersonInfo();
            user.setUserId(customerId);
            userAwardMap.setUser(user);
        }
        if (userAwardId>-1L){
            userAwardMap.setUserAwardId(userAwardId);
        }
        if (operator!=null && operator.getUserId()!=0){
            userAwardMap.setOperator(operator);
        }
        return userAwardMap;
    }
}
