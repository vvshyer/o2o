package com.sun.o2o.web.shopadmin;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.o2o.dto.*;
import com.sun.o2o.entity.*;
import com.sun.o2o.enums.UserProductMapStateEnum;
import com.sun.o2o.service.*;
import com.sun.o2o.util.CodeUtil;
import com.sun.o2o.util.HttpServletRequestUtil;
import com.sun.o2o.util.wechat.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/shopadmin")
public class UserProductManagementController {
    @Autowired
    private UserProductMapService userProductMapService;
    @Autowired
    private ProductSellDailyService productSellDailyService;
    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ShopAuthMapService shopAuthMapService;

    @RequestMapping(value = "/listuserproductmapsbyshop",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listUserProductMapsByShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        //获取分页信息
        int pageIndex = HttpServletRequestUtil.getInt(request,"pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request,"pageSize");
        //获取当前的店铺信息
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        //空值校验，主要确保shopId不为空
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop!=null) && (currentShop.getShopId()!=null)){
            //添加查询条件
            UserProductMap userProductMapCondition = new UserProductMap();
            userProductMapCondition.setShop(currentShop);
            String productName = HttpServletRequestUtil.getString(request,"productName");
            if (productName!=null){
                //若前端想按照商品名模糊查询，则传入productName
                Product product = new Product();
                product.setProductName(productName);
                userProductMapCondition.setProduct(product);
            }
            //根据传入的查询数据获取该店铺的商品销售情况
            UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMapCondition,pageIndex,pageSize);
            modelMap.put("userProductMapList" ,ue.getUserProductMapList());
            modelMap.put("count",ue.getCount());
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty pageSize or pageIndex or shopId");
        }
        return modelMap;
    }


    @RequestMapping(value = "/listproductselldailyinfobyshop",method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listProductSellDailyInfoByShop(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        //获取当前的店铺信息
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        //空值校验
        if (currentShop != null && currentShop.getShopId()!= null){
            //添加查询条件
            ProductSellDaily productSellDailyCondition = new ProductSellDaily();
            productSellDailyCondition.setShop(currentShop);
            Calendar calendar = Calendar.getInstance();
            //获取昨天的日期
            calendar.add(Calendar.DATE,-1);
            Date endTime = calendar.getTime();
            //获取7天前的日期
            calendar.add(Calendar.DATE,-6);
            Date beginTime = calendar.getTime();
            //根据传入的查询条件获取该店铺的商品销售情况
            List<ProductSellDaily> productSellDailyList = productSellDailyService
                    .listProductSellDaily(productSellDailyCondition,beginTime,endTime);
            //指定日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //商品名列表，保证唯一性
            HashSet<String> legendData = new HashSet<>();
            //x轴数据
            HashSet<String> xData = new HashSet<>();
            //定义series
            List<EchartSeries> series = new ArrayList<>();
            //日销量列表
            List<Integer> totalList = new ArrayList<>();
            //当前商品名，默认为空
            String currentProductName = "";
            for (int i = 0; i < productSellDailyList.size(); i++){
                ProductSellDaily productSellDaily = productSellDailyList.get(i);
                //自动去重
                legendData.add(productSellDaily.getProduct().getProductName());
                xData.add(sdf.format(productSellDaily.getCreateTime()));
                if (!currentProductName.equalsIgnoreCase(productSellDaily.getProduct().getProductName())
                        && !currentProductName.isEmpty()){
                    //如果currentProductName不等于获取的商品名，或者已遍历到末尾，
                    //则遍历到下一个商品的日销量信息了，将前一轮遍历的信息放入series中
                    //包括了商品名以及与商品对应的统计日期以及当日销量
                    EchartSeries es = new EchartSeries();
                    es.setName(currentProductName);
                    es.setData(totalList.subList(0,totalList.size()));
                    series.add(es);
                    //重置totalList
                    totalList = new ArrayList<>();
                    //变换下currentProductId为当前的productId
                    currentProductName = productSellDaily.getProduct().getProductName();
                    //继续添加新的值
                    totalList.add(productSellDaily.getTotal());
                }else {
                    //如果还是当前的productId则继续添加新的值
                    totalList.add(productSellDaily.getTotal());
                    currentProductName = productSellDaily.getProduct().getProductName();
                }
                //队列末尾需要将最后一个商品销量信息添加上
                if (i == productSellDailyList.size() - 1){
                    EchartSeries es = new EchartSeries();
                    es.setName(currentProductName);
                    es.setData(totalList.subList(0,totalList.size()));
                    series.add(es);
                }
            }
            modelMap.put("series",series);
            modelMap.put("legendData",legendData);
            //拼接处xAxis
            List<EchartXAxis> xAxis = new ArrayList<>();
            EchartXAxis exa = new EchartXAxis();
            exa.setData(xData);
            xAxis.add(exa);
            modelMap.put("xAxis",xAxis);
            modelMap.put("success",true);
        }else {
            modelMap.put("success",false);
            modelMap.put("errMsg","empty shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/adduserproductmap",method = RequestMethod.GET)
    private String addUserProductMap(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //获取微信授权信息
        WechatAuth auth = CodeUtil.getOperatorInfo(request,wechatAuthService);
        if (auth != null){
            PersonInfo operator = auth.getPersonInfo();
            request.getSession().setAttribute("user",operator);
            //获取二维码里state携带的content信息并解码
            String qrCodeinfo = new String(URLDecoder.decode(HttpServletRequestUtil.getString(request,"state"),"UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            WechatInfo wechatInfo = null;
            try {
                //将解码后的内容用aaa去替换掉之前生成二维码的时候加入的aaa前缀，转换成WechatInfo
                wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa","\""),WechatInfo.class);
            } catch (Exception e) {
                return "shop/operationfail";
            }
            //校验二维码是否已经过期
            if (!CodeUtil.checkQRCodeInfo(wechatInfo)){
                return "shop/operationfail";
            }
            //获取添加消费记录所需要的参数并组建成userproductmap实例
            Long productId = wechatInfo.getProductId();
            Long customerId = wechatInfo.getCustomerId();
            UserProductMap userProductMap = compactUserProduct4Add(customerId,productId,auth.getPersonInfo());
            if (userProductMap != null && customerId!=-1){
                try {
                    if (!checkShopAuth(operator.getUserId(),userProductMap)){
                        return "shop/operationfail";
                    }
                    //添加消费记录
                    UserProductMapExecution se = userProductMapService.addUserProductMap(userProductMap);
                    if (se.getState() == UserProductMapStateEnum.SUCCESS.getState()){
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
     * @param userProductMap
     * @return
     */
    private boolean checkShopAuth(Long userId, UserProductMap userProductMap) {
        //获取该店铺的所有授权信息
        ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService
                .listShopAuthMapByShopId(userProductMap.getShop().getShopId(),1,1000);
        for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()){
            //看看是否给过该人员进行授权
            if (shopAuthMap.getEmployee().getUserId() == userId){
                return true;
            }
        }
        return false;
    }

    /**
     * 更具传入的customerId,productId以及操作员信息组建用户消费记录
     * @param customerId
     * @param productId
     * @param operator
     * @return
     */
    private UserProductMap compactUserProduct4Add(Long customerId, Long productId, PersonInfo operator) {
        UserProductMap userProductMap = null;
        if (customerId != null && productId != null){
            userProductMap = new UserProductMap();
            PersonInfo customer = new PersonInfo();
            customer.setUserId(customerId);
            //主要为了获取商品积分
            Product product = productService.getProductById(productId);
            userProductMap.setProduct(product);
            userProductMap.setShop(product.getShop());
            userProductMap.setUser(customer);
            userProductMap.setPoint(product.getPoint());
            userProductMap.setCreateTime(new Date());
        }
        return userProductMap;
    }


}
