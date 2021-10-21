package com.manura.foodapp.FoodAppConfigServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class FoodAppConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodAppConfigServerApplication.class, args);
	}

}
