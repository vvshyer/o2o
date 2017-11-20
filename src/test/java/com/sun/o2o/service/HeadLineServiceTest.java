package com.sun.o2o.service;

import com.sun.o2o.entity.HeadLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HeadLineServiceTest {
	@Autowired
	private HeadLineService headLineService;

	@Test
	public void testGetHeadLineList() {
		List<HeadLine> headLineList = headLineService.getHeadLineList(new HeadLine());
		System.out.println(headLineList.get(0).getLineName());
		HeadLine headLineCondition = new HeadLine();
		headLineCondition.setEnableStatus(1);
		headLineList = headLineService.getHeadLineList(headLineCondition);
		System.out.println(headLineList.get(0).getLineName());
	}
}
