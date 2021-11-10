package com.manura.foodapp.RabbitMQ_Config.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ_for_Security {
	@Bean
	public FanoutExchange food_app_user_security_Ex() {
		return new FanoutExchange("food-app-user-security", true, false);
	}
	
	@Bean
	public Binding binding_user_security_1() {
		return BindingBuilder.bind(user_security_foodHut()).to(food_app_user_security_Ex());
	}
	
	@Bean
	public Binding binding_user_security_2() {
		return BindingBuilder.bind(user_security_order()).to(food_app_user_security_Ex());
	}
	@Bean
	public Binding binding_user_security_3() {
		return BindingBuilder.bind(user_security_food()).to(food_app_user_security_Ex());
	}
	@Bean
	public Binding binding_user_security_4() {
		return BindingBuilder.bind(user_security_foodapp_review()).to(food_app_user_security_Ex());
	}
	@Bean
	public Queue user_security_food() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_security-food", false, false, false, args);
	}
	@Bean
	public Queue user_security_order() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_security-order", false, false, false, args);
	}
	@Bean
	public Queue user_security_foodHut() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_security-foodHut", false, false, false, args);
	}
	@Bean
	public Queue user_security_foodapp_review() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_security-foodapp_review", false, false, false, args);
	}
}
