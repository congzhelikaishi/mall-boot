package com.testweb.mall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis相关配置
 */
@Configuration
@MapperScan({"com.testweb.mall.mapper", "com.testweb.mall.dao"})
public class MyBatisConfig {
}
