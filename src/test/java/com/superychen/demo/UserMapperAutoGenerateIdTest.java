package com.superychen.demo;

import com.superychen.demo.mybatis.entity.demo.User;
import com.superychen.demo.mybatis.mapper.demo.UserMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperAutoGenerateIdTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
        //插入
        User user = new User();
        user.setName("test2");
        user.setAge(18);
        userMapper.insert(user);
        assert 2 == user.getId();
    }

}
