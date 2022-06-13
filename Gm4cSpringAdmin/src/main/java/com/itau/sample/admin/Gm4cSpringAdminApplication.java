package com.itau.sample.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@EnableAdminServer
@SpringBootApplication
public class Gm4cSpringAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(Gm4cSpringAdminApplication.class, args);
	}

}
