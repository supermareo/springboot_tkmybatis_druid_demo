package com.superychen.demo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.superychen.demo.mybatis.entity.demo.User;
import com.superychen.demo.mybatis.mapper.demo.UserMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageHelperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {

        PageHelper.startPage(1, 10);
        Page<User> users = (Page<User>) userMapper.selectAll();
        assert 11 == users.getTotal();
        assert 1 == users.getPageNum();
        assert 10 == users.getPageSize();
        assert 2 == users.getPages();
        assert 10 == users.getResult().size();

        PageHelper.startPage(2, 10);
        Page<User> users2 = (Page<User>) userMapper.selectAll();
        assert 11 == users2.getTotal();
        assert 2 == users2.getPageNum();
        assert 10 == users2.getPageSize();
        assert 2 == users2.getPages();
        assert 1 == users2.getResult().size();
    }

}
