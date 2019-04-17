package com.superychen.demo.mybatis.mapper.demo;

import com.superychen.demo.mybatis.entity.demo.User;

import org.springframework.stereotype.Repository;

import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserMapper extends Mapper<User> {
}
