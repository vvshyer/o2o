package com.sun.o2o.web.shopadmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.o2o.dto.AwardExecution;
import com.sun.o2o.dto.ImageHolder;
import com.sun.o2o.entity.Award;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.enums.AwardStateEnum;
import com.sun.o2o.exceptions.AwardOperationException;
import com.sun.o2o.service.AwardService;
import com.sun.o2o.util.CodeUtil;
import com.sun.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class AwardManagementController {
    @Autowired
    private AwardService awardService;

    /**
     * 通过店铺id获取该店铺下的商品列表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getawardlistbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAwardListByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 获取前台传过来的页码
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        // 获取前台传过来的每页要求返回的奖品数上限
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        // 从当前session中获取店铺信息，主要是获取shopId
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        // 空值判断
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
            // 获取传入的需要检索的条件，包括是否需要从某个商品类别以及模糊查找商品名去筛选某个店铺下的商品列表
            // 筛选的条件可以进行排列组合
            String awardName = HttpServletRequestUtil.getString(request, "awardName");
            Award awardCondition = compactAwardCondition4Search(currentShop.getShopId(), awardName);
            // 传入查询条件以及分页信息进行查询，返回相应商品列表以及总数
            AwardExecution pe = awardService.getAwardList(awardCondition, pageIndex, pageSize);
            modelMap.put("awardList", pe.getAwardList());
            modelMap.put("count", pe.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }

    /**
     * 通过商品id获取商品信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getawardbyid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getAwardById(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        long awardId = HttpServletRequestUtil.getLong(request,"awardId");
        // 非空判断
        if (awardId > -1) {
            // 获取商品信息
            Award award = awardService.getAwardById(awardId);
            modelMap.put("award", award);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty awardId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/addaward", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> addAward(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 验证码校验
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        // 接收前端参数的变量的初始化，包括商品，缩略图，详情图列表实体类
        ObjectMapper mapper = new ObjectMapper();
        Award award = null;
        String awardStr = HttpServletRequestUtil.getString(request,"awardStr");
        ImageHolder thumbnail = null;
        List<ImageHolder> awardImgList = new ArrayList<ImageHolder>();
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        try {
            // 若请求中存在文件流，则取出相关的文件
            if (multipartResolver.isMultipart(request)) {
                thumbnail = handleImage(request, thumbnail);
            }
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        try {
            // 尝试获取前端传过来的表单string流并将其转换成Award实体类
            award = mapper.readValue(awardStr, Award.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        // 若Award信息，缩略图为非空，则开始进行奖品添加操作
        if (award != null && thumbnail != null) {
            try {
                // 从session中获取当前店铺的Id并赋值给award，减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                award.setShopId(currentShop.getShopId());
                // 执行添加操作
                AwardExecution ae = awardService.addAward(award, thumbnail);
                if (ae.getState() == AwardStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", ae.getStateInfo());
                }
            } catch (AwardOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入奖品信息");
        }
        return modelMap;
    }

    private ImageHolder handleImage(HttpServletRequest request, ImageHolder thumbnail)
            throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 取出缩略图并构建ImageHolder对象
        CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
        if (thumbnailFile != null) {
            thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
        }
        return thumbnail;
    }

    /**
     * 商品编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/modifyaward", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyAward(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 是商品编辑时候调用还是上下架操作的时候调用
        // 若为前者则进行验证码判断，后者则跳过验证码判断
        boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
        // 验证码判断
        if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入了错误的验证码");
            return modelMap;
        }
        // 接收前端参数的变量的初始化，包括商品，缩略图，详情图列表实体类
        ObjectMapper mapper = new ObjectMapper();
        Award award = null;
        ImageHolder thumbnail = null;
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
                request.getSession().getServletContext());
        // 若请求中存在文件流，则取出相关的文件（包括缩略图和详情图）
        try {
            if (multipartResolver.isMultipart(request)) {
                thumbnail = handleImage(request, thumbnail);
            }
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        try {
            String awardStr = HttpServletRequestUtil.getString(request, "awardStr");
            // 尝试获取前端传过来的表单string流并将其转换成Award实体类
            award = mapper.readValue(awardStr, Award.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        // 非空判断
        if (award != null) {
            try {
                // 从session中获取当前店铺的Id并赋值给award，减少对前端数据的依赖
                Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
                award.setShopId(currentShop.getShopId());
                // 开始进行商品信息变更操作
                AwardExecution ae = awardService.modifyAward(award, thumbnail);
                if (ae.getState() == AwardStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", ae.getStateInfo());
                }
            } catch (RuntimeException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }

        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "请输入商品信息");
        }
        return modelMap;
    }

    /**
     * 封装商品查询条件到Award实例中
     *
     * @param shopId(mandatory)
     * @param awardName(optional)
     * @return
     */
    private Award compactAwardCondition4Search(long shopId, String awardName) {
        Award awardCondition = new Award();
        awardCondition.setShopId(shopId);
        // 若有商品名模糊查询的要求则添加进去
        if (awardName != null) {
            awardCondition.setAwardName(awardName);
        }
        return awardCondition;
    }
}
