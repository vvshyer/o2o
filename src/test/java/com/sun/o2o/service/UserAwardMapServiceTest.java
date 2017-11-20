package com.sun.o2o.service;

import com.sun.o2o.dto.UserAwardMapExecution;
import com.sun.o2o.entity.PersonInfo;
import com.sun.o2o.entity.UserAwardMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserAwardMapServiceTest {
    @Autowired
    private UserAwardMapService userAwardMapService;

    @Test
    public void testList(){
        UserAwardMap userAwardMap = new UserAwardMap();
        PersonInfo user = new PersonInfo();
        user.setUserId(1L);
        userAwardMap.setUser(user);
        UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMap,0,3);
        System.out.println(ue.getCount());
    }
}
