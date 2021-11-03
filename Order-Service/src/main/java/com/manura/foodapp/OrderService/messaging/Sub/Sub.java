package com.manura.foodapp.OrderService.messaging.Sub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.OrderService.Service.Impl.OrderServiceImpl;
import com.manura.foodapp.OrderService.Table.FoodTable;
import com.manura.foodapp.OrderService.Table.UserTable;

import reactor.core.publisher.Mono;

@Service
public class Sub {
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	private static final Logger LOG = LoggerFactory.getLogger(Sub.class);
	
	@RabbitListener(queues = "user_created-order",concurrency = "20")
	public void user_created_order(String message) {
		try {
			UserTable user = objectMapper.readValue(message, UserTable.class);
			orderServiceImpl.saveUser(Mono.just(user)).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "user_updated-order",concurrency = "20")
	public void user_updated_order(String message) {
		try {
			UserTable user = objectMapper.readValue(message, UserTable.class);
			orderServiceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "user_security-order",concurrency = "20")
	public void user_security_order(String message) {
		try {
			UserTable user = objectMapper.readValue(message, UserTable.class);
			orderServiceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "food_created-order",concurrency = "20")
	public void food_created_order(String message) {
		try {
			FoodTable food = objectMapper.readValue(message, FoodTable.class);
			orderServiceImpl.saveFood(Mono.just(food)).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "food_updated-order",concurrency = "20")
	public void food_updated_order(String message) {
		try {
			FoodTable food = objectMapper.readValue(message, FoodTable.class);
			orderServiceImpl.updateFood(food.getPublicId(),Mono.just(food)).subscribe();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			LOG.error("Error is {}", e.getMessage());
		}
	}
}