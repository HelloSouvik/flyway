package com.test.flyway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages  = "com.test")
public class SpringBootStandaloneApplication implements CommandLineRunner {
	
	private static Logger LOGGER = LoggerFactory.getLogger(SpringBootStandaloneApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootStandaloneApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println("Test");
		LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>..");
	}
}