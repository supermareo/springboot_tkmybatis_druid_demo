# SpringBoot+TkMybatis+Druid单数据源与多数据源配置

## 环境与工具

```
开发工具：Intellij Idea
包管理工具：maven
JDK版本：1.8.0_201
SpringBoot版本：2.1.0.RELEASE
Druid版本：1.1.12
TkMybatis版本：1.0.5
```

## 第一步：使用[idea]()创建springboot项目

![1555482043176](imgs\1555482043176.png)

![使用maven作为包管理工具](imgs\1555471427955.png) 

![使用lombok插件简化getter、setter方法](imgs\1555471468044.png)

![](imgs\1555471510586.png)

![创建完成的项目结构如下](imgs\1555471649656.png)

## 第二步：先配置下pom，添加所有用到的依赖

```xml
<!--依赖配置-添加下面的依赖-->
<!--日志依赖-->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>2.11.2</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-to-slf4j</artifactId>
    <version>2.10.0</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.25</version>
</dependency>
<!--数据库依赖-->
<dependency>
	<groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!--druid-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.12</version>
</dependency>
<!--tk mybatis-->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
    <version>2.0.4</version>
</dependency>
<!--分页-->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.10</version>
</dependency>
```



## 第三步：先配置下```log4j```日志

### 在resource文件夹下创建```log4j.properties```

```properties
#日志文件路径
log.path.base=/data/log/demo
#log4j.rootLogger=等级,[appender名称],[appender名称]...
log4j.rootLogger=DALI_ROLLING_FILE,CONSOLE
#控制台输出日志：ConsoleAppender
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.Threshold=DEBUG
log4j.appender.CONSOLE.Target=System.out
log4j.appender.CONSOLE.Encoding=UTF-8
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%l][%t]: %m%n
#文件按日回滚日志：DailyRollingFileAppender
log4j.appender.DALI_ROLLING_FILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.DALI_ROLLING_FILE.Threshold=DEBUG
log4j.appender.DALI_ROLLING_FILE.ImmediateFlush=true
log4j.appender.DALI_ROLLING_FILE.Append=true
log4j.appender.DALI_ROLLING_FILE.File=${log.path.base}/log.log
log4j.appender.DALI_ROLLING_FILE.layout=org.apache.log4j.PatternLayout
log4j.appender.DALI_ROLLING_FILE.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %-5p [%l][%t]: %m%n
```

## 第四步：[```TkMybatis```](https://github.com/abel533/Mapper)+[```Druid```](https://github.com/alibaba/druid/)单数据源配置

### 1.修改```resource/application.properties```，添加数据源配置信息

```properties
demo.datasource.type=com.alibaba.druid.pool.DruidDataSource
demo.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
demo.datasource.url=jdbc:mysql://localhost:3306/demo
demo.datasource.username=root
demo.datasource.password=root
demo.datasource.maxActive:20
demo.datasource.initialSize:1
demo.datasource.maxWait:60000
demo.datasource.minIdle:1
demo.datasource.timeBetweenEvictionRunsMillis:60000
demo.datasource.minEvictableIdleTimeMillis:300000
demo.datasource.validationQuery:select 'x'
demo.datasource.testWhileIdle:true
demo.datasource.testOnBorrow:false
demo.datasource.testOnReturn:false
#如果是oracle 则可以设置为true  如果是mysql则设置false 分库分表较多的数据库，建议配置为false。
demo.datasource.poolPreparedStatements:false
demo.datasource.maxOpenPreparedStatements:20
```

### 2.新建```config/datasource```包，并创建```DemoDataSourceConfig```类

```java
package com.superychen.demo.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.spring.annotation.MapperScan;

@Slf4j
@Configuration
//配置mapper扫描的包路径，注意MapperScan来源 tk.mybatis.spring.annotation.MapperScan
@MapperScan(basePackages = DemoDataSourceConfig.MAPPER_PACKAGE, sqlSessionFactoryRef = DemoDataSourceConfig.SESSION_FACTORY)
public class DemoDataSourceConfig {

    static final String SESSION_FACTORY = "demoSqlSessionFactory";
    private static final String DATASOURCE_NAME = "demoDataSource";

    //mapper类的包路径
    static final String MAPPER_PACKAGE = "com.superychen.demo.mybatis.mapper.demo";
    //自定义mapper的xml文件路径
    private static final String MAPPER_XML_PATH = "classpath:/mapper_diy/DemoMapper.xml";
    //数据源配置的前缀，必须与application.properties中配置的对应数据源的前缀一致
    private static final String DATASOURCE_PREFIX = "demo.datasource";

    @Primary
    @Bean(name = DATASOURCE_NAME)
    @ConfigurationProperties(prefix = DATASOURCE_PREFIX)
    public DruidDataSource druidDataSource() {
        return new DruidDataSource();
    }

    @Primary
    @Bean(name = SESSION_FACTORY)
    public SqlSessionFactory sqlSessionFactory() {
        final SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(druidDataSource());
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            //自定义mapper的xml文件地址，当通用mapper提供的默认功能无法满足我们的需求时，可以自己添加实现，与mybatis写mapper一样
            sessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_XML_PATH));
            return sessionFactoryBean.getObject();
        } catch (Exception e) {
            log.error("配置demo的SqlSessionFactory失败，error:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
```

### 3.在```resource```文件夹下创建```mapper_diy```文件夹，用于存放自定义*Mapper.xml文件，创建```DemoMapper.xml```文件，同时在mybatis.mapper.demo包下创建```DemoMapper```类

> ```DemoMapper.xml```
>
> 路径与```DemoDataSourceConfig```中MAPPER_XML_PATH的值要一致

```xml
<?xml version="1.0" encoding="UTF-8" ?><!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<!--一个mapper对应一个数据源-->
<mapper namespace="com.superychen.demo.mybatis.mapper.demo.DemoMapper">
    <!--自定义方法在这里添加，与普通mybatis书写方式完全一样-->
</mapper>
```

> ```DemoMapper.java```
>
> 所在包与```DemoDataSourceConfig```中MAPPER_PACKAGE的值要一致
>
> 所在位置与```DemoMapper.xml```中mapper的namespace值一致

```java
package com.superychen.demo.mybatis.mapper.demo;

import org.springframework.stereotype.Component;

@Component
public interface DemoMapper {
    //自定义方法在这里添加，与普通mybatis书写方式完全一样
}
```

> 到这里，其实已经可以使用了，在本地创建```demo```数据库，并添加```user```表如下

```mysq
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `demo`;
-- 创建user表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;		
```

> 在```src/test/java/com/superychen/demo```下创建如下测试类

```java
package com.superychen.demo;

import com.superychen.demo.mybatis.mapper.demo.DemoMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test1 {

    @Autowired
    private DemoMapper demoMapper;

    @Test
    public void contextLoads() {
        demoMapper.insert();
        assert "superychen".equals(demoMapper.select());
    }

}
```

> 执行测试，测试通过

![测试结果](imgs\1555474868051.png)

> 查看数据库

![数据库](imgs\1555474912920.png)

### 4. 实际使用

> 使用```TkMybatis```的最主要目的是简化我们书写mapper的流程，我们只需要定义数据库表对应的实体类，并定义一个```*Mapper```接口继承其提供的```Mapper<T>```接口，将实体类作为泛型类型，即可使用通用的Mapper操作数据库表方法，如增删改查等

> 以前一步创建的user表为例，在```com.superychen.demo.mybatis.entity.demo```包下创建```User```类

```java
package com.superychen.demo.mybatis.entity.demo;

import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "user")
public class User {
	
    @Id
    private Integer id;
    private String name;
    private Integer age;
    
}
```

> 在```com.superychen.demo.mybatis.mapper.demo```包下创建```UserMapper```接口

```java
package com.superychen.demo.mybatis.mapper.demo;

import com.superychen.demo.mybatis.entity.demo.User;

import org.springframework.stereotype.Repository;

import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserMapper extends Mapper<User> {
}
```

> 查看```UserMapper```的结构，发现有以下方法，具体方法使用请参考官网

![通用Mapper提供的方法](imgs\1555475425212.png)

> 在```src/test/java/com/superychen/demo```下创建如下测试类

```
package com.superychen.demo;

import com.superychen.demo.mybatis.entity.demo.User;
import com.superychen.demo.mybatis.mapper.demo.UserMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
        //所有
        List<User> users = userMapper.selectAll();
        assert 1 == users.size();
        //插入
        User user = new User();
        user.setName("test2");
        user.setAge(18);
        userMapper.insert(user);
        //数量
        int count = userMapper.selectCountByExample(null);
        assert 2 == count;
        //删除
        userMapper.delete(user);
        //数量
        count = userMapper.selectCountByExample(null);
        assert 1 == count;
    }

}
```

> 执行测试，测试通过

![测试userMapper](imgs\1555475652529.png)

> 该数据库下所有表都可以按```UserMapper```的方式进行配置与使用

> 如果需要在插入后获取自增的主键```id```，只需在```id```字段上添加```@GeneratedValue(strategy = GenerationType.IDENTITY)```注解，如下

```java
package com.superychen.demo.mybatis.entity.demo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer age;

}
```

> 在```src/test/java/com/superychen/demo```下创建如下测试类

```java
package com.superychen.demo;

import com.superychen.demo.mybatis.entity.demo.User;
import com.superychen.demo.mybatis.mapper.demo.UserMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperAutoGenerateIdTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
        //插入
        User user = new User();
        user.setName("test2");
        user.setAge(18);
        userMapper.insert(user);
        assert 2 == user.getId();
    }

}
```

> 执行测试，测试通过

![测试结果](imgs\1555476014311.png)

> 查看数据库

![数据库](imgs\1555476040407.png)

## 第五步：多数据源配置

> 多数据源配置只需仿照第一个数据源的方式同样配置即可，此处以新增```Second```数据库的数据源为例

### 1.修改```resource/application.propertie```，添加数据源配置信息

```properties
second.datasource.type=com.alibaba.druid.pool.DruidDataSource
second.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
second.datasource.url=jdbc:mysql://localhost:3306/second
second.datasource.username=root
second.datasource.password=root
second.datasource.maxActive:20
second.datasource.initialSize:1
second.datasource.maxWait:60000
second.datasource.minIdle:1
second.datasource.timeBetweenEvictionRunsMillis:60000
second.datasource.minEvictableIdleTimeMillis:300000
second.datasource.validationQuery:select 'x'
second.datasource.testWhileIdle:true
second.datasource.testOnBorrow:false
second.datasource.testOnReturn:false
#如果是oracle 则可以设置为true  如果是mysql则设置false 分库分表较多的数据库，建议配置为false。
second.datasource.poolPreparedStatements:false
second.datasource.maxOpenPreparedStatements:20
```

### 2.在```config/datasource```包下创建```SecondDataSourceConfig```类

> 去掉```druidDataSource```与```sqlSessionFactory```上的```@Primary```注解

```java
package com.superychen.demo.config.datasource;

import com.alibaba.druid.pool.DruidDataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;
import tk.mybatis.spring.annotation.MapperScan;

@Slf4j
@Configuration
@MapperScan(basePackages = SecondDataSourceConfig.MAPPER_PACKAGE, sqlSessionFactoryRef = SecondDataSourceConfig.SESSION_FACTORY)
public class SecondDataSourceConfig {

    static final String SESSION_FACTORY = "secondSqlSessionFactory";
    private static final String DATASOURCE_NAME = "secondDataSource";

    //mapper类的包路径
    static final String MAPPER_PACKAGE = "com.superychen.demo.mybatis.mapper.second";
    //自定义mapper的xml文件路径
    private static final String MAPPER_XML_PATH = "classpath:/mapper_diy/SecondMapper.xml";
    //数据源配置的前缀，必须与application.properties中配置的对应数据源的前缀一致
    private static final String DATASOURCE_PREFIX = "second.datasource";

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
            //自定义mapper的xml文件地址，当通用mapper提供的默认功能无法满足我们的需求时，可以自己添加实现，与mybatis写mapper一样
            sessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_XML_PATH));
            return sessionFactoryBean.getObject();
        } catch (Exception e) {
            log.error("配置second的SqlSessionFactory失败，error:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
```

### 3.在```resource/mapper_diy```文件夹下创建```SecondMapper.xml```文件，同时在mybatis.mapper.second包下创建```SecondMapper```类

> ```SecondMapper.xml```

```xml
<?xml version="1.0" encoding="UTF-8" ?><!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd" ><!--一个mapper对应一个数据库-->
<mapper namespace="com.superychen.demo.mybatis.mapper.second.SecondMapper">

</mapper>
```

> ```SecondMapper```类

```java
package com.superychen.demo.mybatis.mapper.second;

import org.springframework.stereotype.Component;

@Component
public interface SecondMapper {

    @Insert("INSERT INTO test(name,age) VALUES('superychen',22)")
    int insert();

    @Select("SELECT name FROM test LIMIT 1")
    String select();

}
```

### 4.测试

> 创建```second```数据库，并创建```test```表

```mysql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `second`;
-- 创建user表
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;		
```

> 在```src/test/java/com/superychen/demo```下创建如下测试类

```java
package com.superychen.demo;

import com.superychen.demo.mybatis.mapper.demo.DemoMapper;
import com.superychen.demo.mybatis.mapper.second.SecondMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Test2 {

    @Autowired
    private SecondMapper secondMapper;

    @Test
    public void contextLoads() {
        secondMapper.insert();
        assert "superychen".equals(secondMapper.select());
    }

}
```

> 执行测试，测试通过

![测试结果](imgs\1555480316525.png)

> 查看数据库

![数据库](imgs\1555480268575.png)

## 第六步：配置分布式事务支持

### 1.修改```pom.xml```文件，引入```maven```依赖

```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-commons</artifactId>
    <version>2.1.2.RELEASE</version>
</dependency>
```

### 2.在数据源配置类中添加如下代码

```java
//DataSourceTransactionManager的名称，建议按照数据库的名称+TransactionManager驼峰命名的方式赋值
//如demo数据库，命名如下
private static final String TRANSACTION_MANAGER_NAME = "demoTransactionManager";

//主数据源添加@Primary注解，其它数据源不能添加
@Primary
@Bean(name = TRANSACTION_MANAGER_NAME)
public DataSourceTransactionManager transactionManager() {
    return new DataSourceTransactionManager(druidDataSource());
}
```

### 3.在```config/datasource```包下创建```DataSourcesAutoConfiguration```类

```java
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
```

## 第七步：集成[```PageHelper```](https://github.com/pagehelper/pagehelper-spring-boot)

> ```PageHelper```的集成十分简单，示例代码

### 1.在数据库表(```demo.user```)中插入几条测试数据

```mysql
INSERT INTO `user`(name,age) VALUES ('test3', '20');
INSERT INTO `user`(name,age) VALUES ('test4', '22');
INSERT INTO `user`(name,age) VALUES ('test5', '31');
INSERT INTO `user`(name,age) VALUES ('test6', '12');
INSERT INTO `user`(name,age) VALUES ('test7', '18');
INSERT INTO `user`(name,age) VALUES ('test8', '21');
INSERT INTO `user`(name,age) VALUES ('test9', '29');
INSERT INTO `user`(name,age) VALUES ('test10', '20');
INSERT INTO `user`(name,age) VALUES ('test11', '18');
```

> 数据库表中当前数据如下

![数据库](imgs\1555481274788.png)

### 2.在```src/test/java/com/superychen/demo```下创建如下测试类

```java
package com.superychen.demo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.superychen.demo.mybatis.entity.demo.User;
import com.superychen.demo.mybatis.mapper.demo.UserMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PageHelperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {

        PageHelper.startPage(1, 10);
        Page<User> users = (Page<User>) userMapper.selectAll();
        assert 11 == users.getTotal();
        assert 1 == users.getPageNum();
        assert 10 == users.getPageSize();
        assert 2 == users.getPages();
        assert 10 == users.getResult().size();

        PageHelper.startPage(2, 10);
        Page<User> users2 = (Page<User>) userMapper.selectAll();
        assert 11 == users2.getTotal();
        assert 2 == users2.getPageNum();
        assert 10 == users2.getPageSize();
        assert 2 == users2.getPages();
        assert 1 == users2.getResult().size();
    }

}
```

### 3.执行测试，测试通过

![1555482003884](imgs\1555482003884.png)

## 总结

本篇文章所有代码已上传至[Github](https://github.com/supermareo/springboot_tkmybatis_druid_demo)