package com.manura.foodapp.FoodHutService;

import org.neo4j.springframework.data.config.EnableNeo4jAuditing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableNeo4jAuditing
public class FoodHutServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodHutServiceApplication.class, args);
	}
}


