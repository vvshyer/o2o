package com.sun.o2o.dao;

import java.util.List;

import com.sun.o2o.entity.PersonInfo;

public interface PersonInfoDao {
	/**
	 * 根据查询条件分页返回用户信息列表
	 * 
	 * @param personInfoCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<PersonInfo> queryPersonInfoList(PersonInfo personInfoCondition, int rowIndex, int pageSize);

	/**
	 * 根据查询条件返回总数，配合queryPersonInfoList使用
	 * 
	 * @param personInfoCondition
	 * @return
	 */
	int queryPersonInfoCount(PersonInfo personInfoCondition);

	/**
	 * 通过用户Id查询用户
	 * 
	 * @param userId
	 * @return
	 */
	PersonInfo queryPersonInfoById(long userId);

	/**
	 * 添加用户信息
	 * 
	 * @param personInfo
	 * @return
	 */
	int insertPersonInfo(PersonInfo personInfo);

	/**
	 * 修改用户信息
	 * 
	 * @param personInfo
	 * @return
	 */
	int updatePersonInfo(PersonInfo personInfo);

}
