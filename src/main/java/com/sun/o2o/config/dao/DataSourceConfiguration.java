package com.sun.o2o.config.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.o2o.util.DESUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyVetoException;

/**
 * 配置datasource到ioc容器
 */

//配置mybatis mapper的扫描路径
@MapperScan("com.sun.o2o.dao")
@Configuration
public class DataSourceConfiguration {

    @Value("${jdbc.driver}")
    private String jdbcDriver;
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.username}")
    private String jdbcUsername;
    @Value("${jdbc.password}")
    private String jdbcPassword;

    /**
     * 生成与spring-dao.xml对应的bean datasource
     */
    @Bean(name = "dataSource")
    public ComboPooledDataSource createDataSource() throws PropertyVetoException {
        //生成datasource实例
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        //跟配置文件一样设置以下信息
        //驱动
        dataSource.setDriverClass(jdbcDriver);

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(DESUtil.getDecryptString(jdbcUsername));
        dataSource.setPassword(DESUtil.getDecryptString(jdbcPassword));
        //配置c3p0连接池的私有属性
        dataSource.setMaxPoolSize(30);
        dataSource.setMinPoolSize(10);
        //关闭连接后不自动commit
        dataSource.setAutoCommitOnClose(false);
        //获取连接超时时间
        dataSource.setCheckoutTimeout(10000);
        //当获取连接失败重试次数
        dataSource.setAcquireRetryAttempts(2);

        return dataSource;
    }
}
