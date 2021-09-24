package com.manura.foodapp.RabbitMQ_Config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQ {
	
	@Bean
	public Queue food_Error() {
		Map<String, Object> args = new HashMap<String, Object>();
		return new Queue("food_error", false, false, false, args);
	}
	
}
