package com.si.upstream.application.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
public class TransactionManagementConfig {
	
	@Bean
	public PlatformTransactionManager platformTransactionManager(@Qualifier("dataSource") DataSource myDataSource) {
		return new DataSourceTransactionManager(myDataSource);
	}

}