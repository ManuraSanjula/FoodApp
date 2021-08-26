package com.manura.foodapp.RabbitMQ_Config.Food;


import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ_Queuses_Food {
	
	@Bean
	public FanoutExchange food_app_foodCreated_Ex() {
		return new FanoutExchange("food-app-foodCreated", true, false);
	}
	
	@Bean
	public FanoutExchange food_app_foodUpdated_Ex() {
		return new FanoutExchange("food-app-foodUpdated", true, false);
	}

	
	@Bean
	public Binding binding_foodUpdated_1() {
		return BindingBuilder.bind(food_updated_Cart()).to(food_app_foodUpdated_Ex());
	}
	
	@Bean
	public Binding binding_foodCreated_1() {
		return BindingBuilder.bind(food_created_Cart()).to(food_app_foodCreated_Ex());
	}
	
	@Bean
	public Binding binding_foodUpdated_2() {
		return BindingBuilder.bind(food_updated_foodHut()).to(food_app_foodUpdated_Ex());
	}
	
	@Bean
	public Binding binding_foodCreated_2() {
		return BindingBuilder.bind(food_created_foodHut()).to(food_app_foodCreated_Ex());
	}
	
	

	@Bean
	public Queue food_created_Cart() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "");
		return new Queue("food_created-Cart", false, false, false, args);
	}
	
	@Bean
	public Queue food_updated_Cart() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "");
		return new Queue("food_updated-Cart", false, false, false, args);
	}
	
	@Bean
	public Queue food_created_foodHut() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "");
		return new Queue("food_created-foodHut", false, false, false, args);
	}
	
	@Bean
	public Queue food_updated_foodHut() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "");
		return new Queue("food_updated-foodHut", false, false, false, args);
	}
	
}
