package com.sun.o2o.web.frontend;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.Product;
import com.sun.o2o.entity.Shop;
import com.sun.o2o.service.ProductService;
import com.sun.o2o.util.CodeUtil;
import com.sun.o2o.util.HttpServletRequestUtil;
import com.sun.o2o.util.ShortNetAddressUtil;
import com.sun.o2o.web.shopadmin.ShopAuthManagementController;
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
public class ProductDetailController {
    @Autowired
    private ProductService productService;

    /**
     * 根据商品Id获取商品详情
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/listproductdetailpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductDetailPageInfo(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        // 获取前台传递过来的productId
        long productId = HttpServletRequestUtil.getLong(request, "productId");
        Product product = null;
        // 空值判断
        if (productId != -1) {
            // 根据productId获取商品信息，包含商品详情图列表
            product = productService.getProductById(productId);
            PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
            if (user == null){
                modelMap.put("needQRCode",false);
            }else {
                modelMap.put("needQRCode",true);
            }
            modelMap.put("product", product);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty productId");
        }
        return modelMap;
    }

    private static String urlPrefix;
    private static String urlMiddle;
    private static String urlSuffix;
    private static String productmapUrl;

    @Value("${wechat.prefix}")
    public void setUrlPrefix(String urlPrefix) {
        ProductDetailController.urlPrefix = urlPrefix;
    }

    @Value("${wechat.middle}")
    public void setUrlMiddle(String urlMiddle) {
        ProductDetailController.urlMiddle = urlMiddle;
    }

    @Value("${wechat.suffix}")
    public void setUrlSuffix(String urlSuffix) {
        ProductDetailController.urlSuffix = urlSuffix;
    }

    @Value("${wechat.productmap.url}")
    public void setAuthUrl(String productmapUrl) {
        ProductDetailController.productmapUrl = productmapUrl;
    }

    @RequestMapping(value = "/generateqrcode4product",method = RequestMethod.GET)
    @ResponseBody
    private void generateQRCode4Product(HttpServletRequest request, HttpServletResponse response){
        //从session里获取商品Id
        long productId = HttpServletRequestUtil.getInt(request,"productId");
        PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
        if (productId!=-1 && user!=null && user.getUserId()!=null){
            //获取当前时间戳，以保证二维码的时间有效性，精确到毫秒
            long timeStamp = System.currentTimeMillis();
            //将商品id和timestamp传入content，赋值到state中，这样微信获取到这些信息后会回传到授权信息的添加方法里
            //加上aaa是为了一会在添加信息的方法里替换这些信息使用
            String content = "{aaaproductIdaaa:"+productId + ",aaacustomerIdaaa:" + user.getUserId() +",aaacreateTimeaaa:"+timeStamp+"}";
            try {
                //将content的信息先进行base64编码以避免特殊字符造成的干扰，之后拼接目标URL
                String longUrl = urlPrefix + productmapUrl + urlMiddle + URLEncoder.encode(content,"UTF-8") + urlSuffix;
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
