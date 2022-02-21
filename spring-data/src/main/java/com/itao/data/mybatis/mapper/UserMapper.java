package com.itao.data.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itao.data.mybatis.bean.Employee;
import org.apache.ibatis.annotations.*;

public interface UserMapper extends BaseMapper<Employee> {

    @Results(id = "id", value = {
            @Result(column = "dept_id", property = "dept", one = @One(select = "com.itao.data.mybatis.mapper.DeptMapper.selectById"))
    })
    @Select("select * from t_user where id = #{id}")
    Employee queryUserById(@Param("id") String id);
}
