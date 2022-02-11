package com.testweb.mall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
MyBatis相关配置
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.testweb.mall.mapper", "com.testweb.mall.dao"})
public class MyBatisConfig {
}
