package com.gm4c.limite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(scanBasePackages = {"com.gm4c.limite","com.gm4c.healthcheck"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class Gm4cLimiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(Gm4cLimiteApplication.class, args);
	}

	
}
