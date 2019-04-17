package com.superychen.demo.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.spring.annotation.MapperScan;

@Slf4j
@Configuration
@MapperScan(basePackages = SecondDataSourceConfig.MAPPER_PACKAGE, sqlSessionFactoryRef = SecondDataSourceConfig.SESSION_FACTORY)
public class SecondDataSourceConfig {

    static final String SESSION_FACTORY = "secondSqlSessionFactory";
    private static final String DATASOURCE_NAME = "secondDataSource";

    static final String MAPPER_PACKAGE = "com.superychen.demo.mybatis.mapper.second";
    private static final String MAPPER_XML_PATH = "classpath:/mapper_diy/SecondMapper.xml";
    private static final String DATASOURCE_PREFIX = "second.datasource";
    private static final String TRANSACTION_MANAGER_NAME = "secondTransactionManager";

    @Bean(name = DATASOURCE_NAME)
    @ConfigurationProperties(prefix = DATASOURCE_PREFIX)
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = SESSION_FACTORY)
    public SqlSessionFactory sqlSessionFactory() {
        final SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(druidDataSource());
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_XML_PATH));
            return sessionFactoryBean.getObject();
        } catch (Exception e) {
            log.error("配置second的SqlSessionFactory失败，error:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Bean(name = TRANSACTION_MANAGER_NAME)
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(druidDataSource());
    }

}