package com.manura.foodapp.RabbitMQ_Config.User;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ_for_Users {

	@Bean
	public FanoutExchange food_app_userCreated_Ex() {
		return new FanoutExchange("food-app-userCreated", true, false);
	}
	
	@Bean
	public FanoutExchange food_app_userUpdated_Ex() {
		return new FanoutExchange("food-app-userUpdated", true, false);
	}

	@Bean
	public Binding binding_user_created_1() {
		return BindingBuilder.bind(user_created_food()).to(food_app_userCreated_Ex());
	}
	
	@Bean
	public Binding binding_user_created_2() {
		return BindingBuilder.bind(user_created_foodHut()).to(food_app_userCreated_Ex());
	}
	
	@Bean
	public Binding binding_user_created_3() {
		return BindingBuilder.bind(user_created_Cart()).to(food_app_userCreated_Ex());
	}
	
	@Bean
	public Binding binding_user_created_4() {
		return BindingBuilder.bind(user_created_order()).to(food_app_userCreated_Ex());
	}
	
	@Bean
	public Binding binding_user_updated_1() {
		return BindingBuilder.bind(user_updated_food()).to(food_app_userUpdated_Ex());
	}
	
	@Bean
	public Binding binding_user_updated_2() {
		return BindingBuilder.bind(user_updated_foodHut()).to(food_app_userUpdated_Ex());
	}
	
	@Bean
	public Binding binding_user_updated_3() {
		return BindingBuilder.bind(user_updated_Cart()).to(food_app_userUpdated_Ex());
	}
	
	@Bean
	public Binding binding_user_updated_4() {
		return BindingBuilder.bind(user_updated_order()).to(food_app_userUpdated_Ex());
	}
	
	@Bean
	public Queue user_created_food() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_created-food", false, false, false, args);
	}

	@Bean
	public Queue user_updated_food() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_updated-food", false, false, false, args);
	}

	
	@Bean
	public Queue user_created_order() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_created-order", false, false, false, args);
	}

	@Bean
	public Queue user_updated_order() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_updated-order", false, false, false, args);
	}

	
	@Bean
	public Queue user_created_Cart() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_created-Cart", false, false, false, args);
	}

	@Bean
	public Queue user_updated_Cart() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_updated-Cart", false, false, false, args);
	}

	@Bean
	public Queue user_created_foodHut() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_created-foodHut", false, false, false, args);
	}

	@Bean
	public Queue user_updated_foodHut() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_updated-foodHut", false, false, false, args);
	}

}