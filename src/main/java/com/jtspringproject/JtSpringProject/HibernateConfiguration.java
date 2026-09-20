package com.jtspringproject.JtSpringProject;

import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
 
@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(HibernateConfiguration.class);

    @Value("${db.driver}")
    private String DRIVER;
 
    @Value("${db.password}")
    private String PASSWORD;
 
    @Value("${db.url}")
    private String URL;
 
    @Value("${db.username}")
    private String USERNAME;
 
    @Value("${hibernate.dialect}")
    private String DIALECT;
 
    @Value("${hibernate.show_sql}")
    private String SHOW_SQL;
 
    @Value("${hibernate.hbm2ddl.auto}")
    private String HBM2DDL_AUTO;
 
    @Value("${entitymanager.packagesToScan}")
    private String PACKAGES_TO_SCAN;
 
    @Bean
    public DataSource dataSource() {
        logger.info("Initializing DataSource with driver={}", DRIVER);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER);
        dataSource.setUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }
 
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        logger.info("Building SessionFactory for packages={}", PACKAGES_TO_SCAN);
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(PACKAGES_TO_SCAN);
        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", DIALECT);
        hibernateProperties.put("hibernate.show_sql", SHOW_SQL);
        hibernateProperties.put("hibernate.hbm2ddl.auto", HBM2DDL_AUTO);
        sessionFactory.setHibernateProperties(hibernateProperties);
 
        return sessionFactory;
    }
 
    @Bean
    public HibernateTransactionManager transactionManager() {
        logger.info("Configuring HibernateTransactionManager");
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }   
}
