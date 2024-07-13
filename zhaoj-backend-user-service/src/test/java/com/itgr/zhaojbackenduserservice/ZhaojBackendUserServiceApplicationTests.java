package com.itgr.zhaojbackenduserservice;

import com.itgr.zhaojbackendmodel.model.entity.User;
import com.itgr.zhaojbackenduserservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ZhaojBackendUserServiceApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUserAccount("liuwei0000");
        user.setUserPassword("123456");
        user.setUserName("666666");
        userService.save(user);
    }

}
