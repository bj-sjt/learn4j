package com.itao.data.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MybatisConfig {

    @Bean
    public MetaObjectHandler metaObjectHandler(){
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                //this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
                // 或者
                this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class); // 起始版本 3.3.3(推荐)
                this.strictInsertFill(metaObject, "modifyTime", LocalDateTime::now, LocalDateTime.class); // 起始版本 3.3.3(推荐)
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                //this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
                // 或者
                this.strictUpdateFill(metaObject, "modifyTime", LocalDateTime::now, LocalDateTime.class); // 起始版本 3.3.3(推荐)
            }
        };
    }
}
