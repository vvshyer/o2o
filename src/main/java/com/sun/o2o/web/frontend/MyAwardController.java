package com.sun.o2o.web.frontend;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sun.o2o.dto.UserAwardMapExecution;
import com.sun.o2o.entity.Award;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.entity.UserAwardMap;
import com.sun.o2o.enums.UserAwardMapStateEnum;
import com.sun.o2o.service.AwardService;
import com.sun.o2o.service.PersonInfoService;
import com.sun.o2o.service.UserAwardMapService;
import com.sun.o2o.util.CodeUtil;
import com.sun.o2o.util.HttpServletRequestUtil;
import com.sun.o2o.util.ShortNetAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/frontend")
public class MyAwardController {
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private PersonInfoService personInfoService;

    /**
     * 根据顾客奖品映射Id获取单条顾客奖品的映射信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getawardbyuserawardid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAwardByUserAwardId(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 获取前台传过来的页码
        long userAwardId = HttpServletRequestUtil.getLong(request,"userAwardId");
        // 空值判断
        if (userAwardId > -1) {
            UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
            Award award = awardService.getAwardById(userAwardMap.getAward().getAwardId());
            modelMap.put("award", award);
            modelMap.put("usedStatus",userAwardMap.getUsedStatus());
            modelMap.put("userAwardMap",userAwardMap);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty awardId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/listuserawardmapsbycustomer", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listUserAwardMapsByCustomer(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //获取分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //从session中获取用户信息
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        //空值判断。确保用户Id非空
        if (pageIndex > -1 && pageSize > -1 && user!=null && user.getUserId()!=null){
            UserAwardMap userAwardMapCondition = new UserAwardMap();
            userAwardMapCondition.setUser(user);
            long shopId = HttpServletRequestUtil.getLong(request,"shopId");
            if (shopId > -1){
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userAwardMapCondition.setShop(shop);
            }
            String awardName = HttpServletRequestUtil.getString(request,"awardName");
            if (awardName != null){
                //若奖品名为非空，则将其添加进查询条件里进行模糊查询
                Award award = new Award();
                award.setAwardName(awardName);
                userAwardMapCondition.setAward(award);
            }
            //根据传入的查询条件分页获取用户奖品映射信息
            UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMapCondition,pageIndex,pageSize);
            modelMap.put("userAwardMapList",ue.getUserAwardMapList());
            modelMap.put("count",ue.getCount());
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or userId");
        }
        return modelMap;
    }


    @RequestMapping(value = "/adduserawardmap", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addAward(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        Long awardId = HttpServletRequestUtil.getLong(request,"awardId");
        UserAwardMap userAwardMap = compactUserAwardMap4Add(user,awardId);
        if (userAwardMap != null){
            try {
                UserAwardMapExecution se = userAwardMapService.addUserAwardMap(userAwardMap);
                if (se.getState() == UserAwardMapStateEnum.SUCCESS.getState()){
                    modelMap.put("success",true);
                }else {
                    modelMap.put("success",false);
                    modelMap.put("errMsg",se.getStateInfo());
                }
            }catch (RuntimeException e){
                modelMap.put("success",false);
                modelMap.put("errMsg",e.getMessage());
                return modelMap;
            }
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","请选择领取的奖品");
        }
        return modelMap;
    }


    /**
     *
     * @param user
     * @param awardId
     * @return
     */
    private UserAwardMap compactUserAwardMap4Add(PersonInfo user, Long awardId) {
        UserAwardMap userAwardMap = new UserAwardMap();
        if (user!=null){
            userAwardMap.setUser(user);
        }
        if (awardId != -1L) {
            Award award = new Award();
            award.setAwardId(awardId);
            userAwardMap.setAward(award);
        }
        return userAwardMap;
    }

    private static String urlPrefix;
    private static String urlMiddle;
    private static String urlSuffix;
    private static String exchangeUrl;

    @Value("${wechat.prefix}")
    public void setUrlPrefix(String urlPrefix) {
        MyAwardController.urlPrefix = urlPrefix;
    }

    @Value("${wechat.middle}")
    public void setUrlMiddle(String urlMiddle) {
        MyAwardController.urlMiddle = urlMiddle;
    }

    @Value("${wechat.suffix}")
    public void setUrlSuffix(String urlSuffix) {
        MyAwardController.urlSuffix = urlSuffix;
    }

    @Value("${wechat.exchange.url}")
    public void setAuthUrl(String exchangeUrl) {
        MyAwardController.exchangeUrl = exchangeUrl;
    }


    @RequestMapping(value = "/generateqrcode4award",method = RequestMethod.GET)
    @ResponseBody
    private void generateQRCode4Award(HttpServletRequest request, HttpServletResponse response){
        //从session里获取商品Id
        long userAwardId = HttpServletRequestUtil.getInt(request,"userAwardId");;
        UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        if (userAwardMap!=null && user!=null && user.getUserId()!=null
                && userAwardMap.getUser().getUserId() == user.getUserId()) {
            //获取当前时间戳，以保证二维码的时间有效性，精确到毫秒
            long timeStamp = System.currentTimeMillis();
            //将商品id和timestamp传入content，赋值到state中，这样微信获取到这些信息后会回传到授权信息的添加方法里
            //加上aaa是为了一会在添加信息的方法里替换这些信息使用
            String content = "{aaauserAwardIdaaa:"+userAwardId + ",aaacustomerIdaaa:" + user.getUserId() +",aaacreateTimeaaa:"+timeStamp+"}";
            try {
                //将content的信息先进行base64编码以避免特殊字符造成的干扰，之后拼接目标URL
                String longUrl = urlPrefix + exchangeUrl + urlMiddle + URLEncoder.encode(content,"UTF-8") + urlSuffix;
                //对目标URL转换成短的URL
                String shortUrl = ShortNetAddressUtil.getShortURL(longUrl);
                //调用二维码生成
                BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(shortUrl,response);
                //将二维码以图片流的形式输出到前端
                MatrixToImageWriter.writeToStream(qRcodeImg,"png",response.getOutputStream());
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
