package com.itao.data.mybatis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itao.data.mybatis.bean.Employee;

public interface UserService extends IService<Employee> {

    Employee queryUserById(String id);
}
