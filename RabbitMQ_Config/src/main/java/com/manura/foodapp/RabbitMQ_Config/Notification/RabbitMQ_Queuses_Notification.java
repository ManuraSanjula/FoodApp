package com.manura.foodapp.RabbitMQ_Config.Notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ_Queuses_Notification {
	@Bean
	public Queue user_verify_email() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_verify_email", false, false, false, args);
	}
	
	@Bean
	public Queue user_password_reset_success() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_password_reset_success", false, false, false, args);
	}
	
	@Bean
	public Queue user_security() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("user_security", false, false, false, args);
	}
	
	@Bean
	public Queue order_confirm() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("order_confirm", false, false, false, args);
	}
	
	@Bean
	public Queue order_completed() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("order_completed", false, false, false, args);
	}
	
	@Bean
	public Queue order_accepted() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("order_accepted", false, false, false, args);
	}
	
	@Bean
	public Queue order_refund_done() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("x-dead-letter-exchange", "food_Error");
		return new Queue("order_refund_done", false, false, false, args);
	}
}