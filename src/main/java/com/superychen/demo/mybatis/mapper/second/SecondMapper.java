package com.superychen.demo.mybatis.mapper.second;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Component
public interface SecondMapper {

    @Insert("INSERT INTO test(name,age) VALUES('superychen',22)")
    int insert();

    @Select("SELECT name FROM test LIMIT 1")
    String select();

}
