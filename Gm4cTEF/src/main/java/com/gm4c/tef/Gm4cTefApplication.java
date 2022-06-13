package com.gm4c.tef;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

//@SpringBootApplication(scanBasePackages = {"com.gm4c.tef","com.gm4c.healthcheck"})
@SpringBootApplication
@EnableRedisRepositories
public class Gm4cTefApplication {

	public static void main(String[] args) {
		SpringApplication.run(Gm4cTefApplication.class, args);
	}
	
}
