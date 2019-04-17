package com.superychen.demo;

import com.superychen.demo.mybatis.mapper.demo.DemoMapper;
import com.superychen.demo.mybatis.mapper.second.SecondMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test2 {

    @Autowired
    private SecondMapper secondMapper;

    @Test
    public void contextLoads() {
        secondMapper.insert();
        assert "superychen".equals(secondMapper.select());
    }

}
