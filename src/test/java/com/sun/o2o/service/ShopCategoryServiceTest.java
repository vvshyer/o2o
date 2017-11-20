package com.sun.o2o.service;

import com.sun.o2o.entity.ShopCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopCategoryServiceTest {
	@Autowired
	private ShopCategoryService shopCategoryService;

	@Test
	public void testGetShopCategoryList() {
		List<ShopCategory> categoryList = shopCategoryService.getShopCategoryList(null);
		System.out.println(categoryList.get(0).getShopCategoryName());
		ShopCategory shopCategoryCondition = new ShopCategory();
		ShopCategory parent = new ShopCategory();
		parent.setShopCategoryId(10L);
		shopCategoryCondition.setParent(parent);
		categoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition);
		System.out.println(categoryList.get(0).getShopCategoryName());
	}
}
