package com.dentner.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = {"com.dentner"})
public class DentnerAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentnerAdminApplication.class, args);
	}

}
