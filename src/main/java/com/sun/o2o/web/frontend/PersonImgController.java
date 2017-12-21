package com.sun.o2o.web.frontend;

import com.sun.o2o.entity.Award;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.UserAwardMap;
import com.sun.o2o.service.PersonInfoService;
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
public class PersonImgController {
    /**
     * 根据顾客奖品映射Id获取单条顾客奖品的映射信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getpersonimg", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getPersonImg(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        // 空值判断
        if (user != null) {
            modelMap.put("personImg", user.getProfileImg());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty user");
        }
        return modelMap;
    }

}
