package com.example.demo;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@PropertySource("classpath:datasource.properties")
@ComponentScan(basePackages = {"com.example.demo"})
public class SpringContextTestConfiguration {

    @Bean
    public DataSource dataSource(final HikariConfig config) {
        return new HikariDataSource(config);
    }

    @Bean
    public HikariConfig hikariConfig(@Value("${datasource.class}") final String dataSource, @Qualifier("hikariProperties") final Properties hikariProperties) {
        final HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(dataSource);
        config.setMaximumPoolSize(10);
        config.setIdleTimeout(30000);
        config.setConnectionTestQuery("select 1 from information_schema.system_users");
        config.setDataSourceProperties(hikariProperties);
        return config;
    }

    @Bean
    @Qualifier("hikariProperties")
    public Properties hikariProperties(@Value("${datasource.url}") final String url, @Value("${datasource.username}") final String username,
            @Value("${datasource.password}") final String password) {
        final Properties properties = new Properties();
        properties.setProperty("url", url);
        properties.setProperty("user", username);
        properties.setProperty("password", password);
        return properties;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(true);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.HSQL);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public LocalValidatorFactoryBean validatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public JpaDialect dialect() {
        return new HibernateJpaDialect();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource, final JpaVendorAdapter jpaVendorAdapter,
            final JpaDialect dialect, final ValidatorFactory validatorFactoryBean) {
        final LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(dataSource);
        emfb.setJpaVendorAdapter(jpaVendorAdapter);
        emfb.setPackagesToScan("com.example.demo");
        return emfb;
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory factory, final DataSource dataSource, final JpaDialect dialect) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(factory);
        transactionManager.setJpaDialect(dialect);
        transactionManager.setDataSource(dataSource);
        return transactionManager;
        
    }
}