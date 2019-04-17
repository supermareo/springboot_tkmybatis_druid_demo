package com.superychen.demo;

import com.superychen.demo.mybatis.entity.demo.User;
import com.superychen.demo.mybatis.mapper.demo.UserMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
        //所有
        List<User> users = userMapper.selectAll();
        assert 1 == users.size();
        //插入
        User user = new User();
        user.setName("test2");
        user.setAge(18);
        userMapper.insert(user);
        //数量
        int count = userMapper.selectCountByExample(null);
        assert 2 == count;
        //删除
        userMapper.delete(user);
        //数量
        count = userMapper.selectCountByExample(null);
        assert 1 == count;
    }

}
