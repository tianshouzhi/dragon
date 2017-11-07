package com.tianshouzhi.dragon.console.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by TIANSHOUZHI336 on 2017/3/23.
 */
@Configuration
public class DatabaseConfig {
    @Bean
    public DataSource dataSource(){
        DruidDataSource druidDataSource=new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/dragon_sharding_00");
        druidDataSource.setUsername("root");
        druidDataSource.setPassword("shxx12151022");
        return druidDataSource;
    }
}
