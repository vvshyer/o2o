package com.sun.o2o.util;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import com.sun.o2o.dto.UserAccessToken;
import com.sun.o2o.dto.WechatInfo;
import com.sun.o2o.entity.WechatAuth;
import com.sun.o2o.service.WechatAuthService;
import com.sun.o2o.util.wechat.WechatUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CodeUtil {
    /**
     * 检查验证码是否和预期相符
     * @param request
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request) {
        String verifyCodeExpected = (String) request.getSession()
                .getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
        String verifyCodeActual = HttpServletRequestUtil.getString(request, "verifyCodeActual");
        if (verifyCodeActual == null || !verifyCodeActual.toLowerCase().equals(verifyCodeExpected.toLowerCase())) {
            return false;
        }
        return true;
    }

    /**
     * 生成二维码的图片流
     * @param content
     * @param response
     * @return
     */
    public static BitMatrix generateQRCodeStream(String content, HttpServletResponse response){
        //给response添加头部信息，告诉浏览器返回的是图片流
        response.setHeader("Cache-Control","no-store");
        response.setHeader("Pragma","no-cache");
        response.setDateHeader("Expires",0);
        response.setContentType("image/png");
        //设置图片的文字编码以及内边框距
        Map<EncodeHintType,Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        hints.put(EncodeHintType.MARGIN,0);
        BitMatrix bitMatrix;
        try {
            //参数顺序：编码内容、编码类型、生成图片宽度、高度、设置参数
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE,300,300,hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        return bitMatrix;
    }

    /**
     * 根据二维码携带的createTime判断是否超过了10分钟，超过则过期
     * @param wechatInfo
     * @return
     */
    public static boolean checkQRCodeInfo(WechatInfo wechatInfo) {
        if (wechatInfo != null && wechatInfo.getShopId()!=null && wechatInfo.getCreateTime()!=null){
            long nowTime = System.currentTimeMillis();
            if ((nowTime - wechatInfo.getCreateTime()) < 600000){
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 根据code获取AccessToken，进而通过token里的openId获取微信用户信息
     * @param request
     * @return
     */
    public static WechatAuth getOperatorInfo(HttpServletRequest request, WechatAuthService wechatAuthService) {
        String code = request.getParameter("code");
        WechatAuth auth = null;
        if (null!=code){
            UserAccessToken token;
            try {
                token = WechatUtil.getUserAccessToken(code);
                String openId = token.getOpenId();
                request.getSession().setAttribute("openId",openId);
                auth = wechatAuthService.getWechatAuthByOpenId(openId);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return auth;
    }


}
