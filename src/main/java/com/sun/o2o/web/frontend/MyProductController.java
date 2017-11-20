package com.sun.o2o.web.frontend;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sun.o2o.dto.UserAwardMapExecution;
import com.sun.o2o.dto.UserProductMapExecution;
import com.sun.o2o.entity.*;
import com.sun.o2o.enums.UserAwardMapStateEnum;
import com.sun.o2o.service.AwardService;
import com.sun.o2o.service.PersonInfoService;
import com.sun.o2o.service.UserAwardMapService;
import com.sun.o2o.service.UserProductMapService;
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
public class MyProductController {
    @Autowired
    private UserProductMapService userProductMapService;


    @RequestMapping(value = "/listuserproductmapsbycustomer", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listUserProductMapsByCustomer(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //获取分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //从session中获取用户信息
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        //空值判断。确保用户Id非空
        if (pageIndex > -1 && pageSize > -1 && user!=null && user.getUserId()!=null){
            UserProductMap userProductMapCondition = new UserProductMap();
            userProductMapCondition.setUser(user);
            long shopId = HttpServletRequestUtil.getLong(request,"shopId");
            if (shopId > -1){
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userProductMapCondition.setShop(shop);
            }
            String productName = HttpServletRequestUtil.getString(request,"productName");
            if (productName != null){
                //若商品名为非空，则将其添加进查询条件里进行模糊查询
                Product product = new Product();
                product.setProductName(productName);
                userProductMapCondition.setProduct(product);
            }
            //根据传入的查询条件分页获取用户商品映射信息
            UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMapCondition,pageIndex,pageSize);
            modelMap.put("userProductMapList",ue.getUserProductMapList());
            modelMap.put("count",ue.getCount());
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or userId");
        }
        return modelMap;
    }
}
