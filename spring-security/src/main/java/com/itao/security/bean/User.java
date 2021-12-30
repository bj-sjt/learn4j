package com.itao.security.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * If you believe this class is safe to deserialize,
 * please provide an explicit mapping using Jackson annotations or by providing a Mixin.
 * If the serialization is only done by a trusted source, you can also enable default typing
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)  // 解决反序列化问题
public class User implements Serializable {

    private String name;
    private String password;
}
