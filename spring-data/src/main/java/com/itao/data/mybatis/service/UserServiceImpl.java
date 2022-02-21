package com.itao.data.mybatis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itao.data.mybatis.bean.Employee;
import com.itao.data.mybatis.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, Employee> implements UserService, IService<Employee> {

    private UserMapper userMapper;

    @Override
    public Employee queryUserById(String id) {

        return userMapper.queryUserById(id);
    }
}
