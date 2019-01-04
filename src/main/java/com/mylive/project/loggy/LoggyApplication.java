package com.mylive.project.loggy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@EnableDiscoveryClient
@Configuration
public class LoggyApplication{

	private static final Logger logger = LoggerFactory.getLogger(LoggyApplication.class);

	public static void main(String[] args) {

		logger.info("--------------------------------------------");
		SpringApplication.run(LoggyApplication.class, args);
		logger.info("--------------------------------------------");
	}

}
