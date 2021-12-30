package com.itao.flux.core;

import com.itao.flux.util.SnowflakeIdHelper;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * 自定义主键生成策略，但是在 reactive jpa 中无效
 */
public class CustomIDGenerator extends IdentityGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor s, Object obj) {
        return SnowflakeIdHelper.getId();
    }
}
