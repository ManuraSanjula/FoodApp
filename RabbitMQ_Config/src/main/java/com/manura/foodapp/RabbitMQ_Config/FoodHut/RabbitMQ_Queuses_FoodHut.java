package com.manura.foodapp.RabbitMQ_Config.FoodHut;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ_Queuses_FoodHut {
	
	@Bean
	public FanoutExchange food_app_foodHutCreated_Ex() {
		return new FanoutExchange("food-app-foodHutCreated", true, false);
	}
	
	@Bean
	public FanoutExchange food_app_foodHutUpdated_Ex() {
		return new FanoutExchange("food-app-foodHutUpdated", true, false);
	}

	@Bean
	public Binding binding1() {
		return BindingBuilder.bind(foodHut_created_food()).to(food_app_foodHutCreated_Ex());
	}
	
	@Bean
	public Binding binding2() {
		return BindingBuilder.bind(foodHut_updated_food()).to(food_app_foodHutUpdated_Ex());
	}
	
	
	@Bean
	public Queue foodHut_created_food() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "");
		return new Queue("foodHut_created-food", false, false, false, args);
	}
	
	@Bean
	public Queue foodHut_updated_food() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "");
		return new Queue("foodHut_updated-food", false, false, false, args);
	}
}
