package com.techolution.cf.haash;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 
 * @author Prithvish Mukherjee
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.techolution.cf.haash" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@EnableJpaRepositories
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	@Profile("cloud")
	public Cloud cloud() {
		return new CloudFactory().getCloud();
	}

	@Bean
	@Profile("local")
	public DataSource localDataSource() {
		DriverManagerDataSource dmds = new DriverManagerDataSource();
		dmds.setUrl("jdbc:mysql://us-cdbr-iron-east-04.cleardb.net:3306/ad_36b16c1a88afbf9");
		dmds.setUsername("b21fbbb77c71b6");
		dmds.setPassword("9a5d29f9");
		dmds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		return dmds;
	}

	@Profile("cloud")
	@Configuration
	@EnableWebMvc
	@EnableAspectJAutoProxy
	class CloudConfig extends AbstractCloudConfig {
		@Bean
		public DataSource hashDbDataSource() {
			// there should be a db-service called hash-db which should be a
			// mySql DB type
			return connectionFactory().dataSource("hash-db");

		}
	}
}
