package com.sun.o2o.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

/**
 * 对标spring-service.xml里面的transactionManager
 * 继承TransactionManagementConfigurer是因为开启annotation-driven
 */
@Configuration
//首先使用注解 @EnableTransactionManagement开启事务之后
//在Service方法上添加注解 @Transactional便可
@EnableTransactionManagement
public class TransactionManagementConfiguration implements TransactionManagementConfigurer{

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @Override
    /**
     * 关于事务管理，需要返回PlatformTransactionManager的实现
     */
    public PlatformTransactionManager annotationDrivenTransactionManager(){
        return new DataSourceTransactionManager(dataSource);
    }
}
