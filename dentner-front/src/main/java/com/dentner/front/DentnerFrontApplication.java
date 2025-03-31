package com.dentner.front;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//@EnableScheduling
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = {"com.dentner"})
public class DentnerFrontApplication {

	public static void main(String[] args) {
		SpringApplication.run(DentnerFrontApplication.class, args);
	}

}
