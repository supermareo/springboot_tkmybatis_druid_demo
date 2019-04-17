package com.superychen.demo.config.datasource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;

/**
 * 配置分布式事务管理
 */
@Configuration
@EnableTransactionManagement
@ConditionalOnBean({
        DemoDataSourceConfig.class,
        SecondDataSourceConfig.class,
        //新增数据源在这里添加
        //XxxDataSourceConfig.class
})
public class DataSourcesAutoConfiguration implements TransactionManagementConfigurer {

    @Resource
    private DemoDataSourceConfig demoDataSourceConfig;
    @Resource
    private SecondDataSourceConfig secondDataSourceConfig;
    //新增数据源在这里添加
    //@Resource
    //private XxxDataSourceConfig xxxDataSourceConfig;

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new ChainedTransactionManager(
                demoDataSourceConfig.transactionManager(),
                secondDataSourceConfig.transactionManager()
                //新增数据源在这里添加
                //xxxDataSourceConfig.xxxTransactionManager()
        );
    }

}