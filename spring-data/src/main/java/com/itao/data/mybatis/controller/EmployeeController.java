package com.itao.data.mybatis.controller;

import com.itao.data.mybatis.bean.Employee;
import com.itao.data.mybatis.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/mybatis")
@AllArgsConstructor
public class EmployeeController {

    private UserService userService;

    @GetMapping("/{id}")
    public Employee getUser(@PathVariable String id){
        return userService.getById(id);
    }

    @GetMapping("/user/{id}")
    public Employee getUserById(@PathVariable String id){
        return userService.queryUserById(id);
    }

    @GetMapping("/employees")
    public List<Employee> getUsers(){
        return userService.list();
    }

    @DeleteMapping("/{id}")
    public String delUser(@PathVariable String id){
        try {
            userService.removeById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @PostMapping("/employee")
    public Employee user(@RequestBody Employee employee){
        userService.save(employee);
        return employee;
    }
}
