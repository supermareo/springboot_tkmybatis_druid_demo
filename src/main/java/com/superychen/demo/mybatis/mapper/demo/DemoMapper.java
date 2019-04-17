package com.superychen.demo.mybatis.mapper.demo;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface DemoMapper {

    @Insert("INSERT INTO user(name,age) VALUES('superychen',22)")
    int insert();

    @Select("SELECT name FROM user LIMIT 1")
    String select();
}
