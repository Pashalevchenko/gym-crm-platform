package com.gym.crm.application.config;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;

@TestConfiguration
public class DbUnitConfig {

    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean config = new DatabaseConfigBean();
        config.setDatatypeFactory(new H2DataTypeFactory());
        config.setQualifiedTableNames(false);

        return config;
    }

    @Bean(name = "dbUnitDatabaseConnection")
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource dataSource, DatabaseConfigBean dbUnitDatabaseConfig) {
        DatabaseDataSourceConnectionFactoryBean bean = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        bean.setSchema("PUBLIC");
        bean.setDatabaseConfig(dbUnitDatabaseConfig);

        return bean;
    }
}