package com.manura.foodapp.RabbitMQ_Config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RabbitMqConfigApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqConfigApplication.class, args);
	}

}
