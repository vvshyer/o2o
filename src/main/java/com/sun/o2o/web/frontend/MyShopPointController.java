package com.sun.o2o.web.frontend;

import com.sun.o2o.dto.UserShopMapExecution;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.entity.UserShopMap;
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
public class MyShopPointController {
    @Autowired
    private UserShopMapService userShopMapService;


    @RequestMapping(value = "/listusershopmapsbycustomer", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listUserShopMapsByCustomer(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        //获取分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //从session中获取用户信息
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        //空值判断。确保用户Id非空
        if (pageIndex > -1 && pageSize > -1 && user!=null && user.getUserId()!=null){
            UserShopMap userShopMapCondition = new UserShopMap();
            userShopMapCondition.setUser(user);
            long shopId = HttpServletRequestUtil.getLong(request,"shopId");
            if (shopId > -1){
                Shop shop = new Shop();
                shop.setShopId(shopId);
                userShopMapCondition.setShop(shop);
            }
            //根据传入的查询条件分页获取用户商品映射信息
            UserShopMapExecution ue = userShopMapService.listUserShopMap(userShopMapCondition,pageIndex,pageSize);
            modelMap.put("userShopMapList",ue.getUserShopMapList());
            System.out.println(ue.getUserShopMapList().size());
            modelMap.put("count",ue.getCount());
            System.out.println(ue.getCount());
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or userId");
        }
        return modelMap;
    }
}
