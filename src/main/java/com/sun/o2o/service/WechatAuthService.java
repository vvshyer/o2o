package com.sun.o2o.service;

import com.sun.o2o.dto.WechatAuthExecution;
import com.sun.o2o.entity.WechatAuth;
import com.sun.o2o.exceptions.WechatAuthOperationException;

public interface WechatAuthService {

	/**
	 * 通过openId查找平台对应的微信帐号
	 * 
	 * @param openId
	 * @return
	 */
	WechatAuth getWechatAuthByOpenId(String openId);

	/**
	 * 注册本平台的微信帐号
	 * 
	 * @param wechatAuth
	 * @return
	 * @throws RuntimeException
	 */
	WechatAuthExecution register(WechatAuth wechatAuth) throws WechatAuthOperationException;

}
