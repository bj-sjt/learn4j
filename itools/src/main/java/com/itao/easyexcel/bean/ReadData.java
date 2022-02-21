package com.itao.easyexcel.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ReadData {
    @ExcelProperty(index = 0)
    private String name;
    @ExcelProperty(index = 1)
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private Date date;
    @ExcelProperty(index = 2)
    private Double salary;
}
