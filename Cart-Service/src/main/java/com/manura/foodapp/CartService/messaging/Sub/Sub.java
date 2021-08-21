package com.manura.foodapp.CartService.messaging.Sub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.CartService.Service.Impl.CartServiceImpl;
import com.manura.foodapp.CartService.Table.FoodTable;
import com.manura.foodapp.CartService.Table.UserTable;


import reactor.core.publisher.Mono;

@Service
public class Sub {
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CartServiceImpl cartServiceImpl;
	
	private static final Logger LOG = LoggerFactory.getLogger(Sub.class);
	
	@RabbitListener(queues = "user_created-Cart")
	public void user_created_Cart(String message) {
		try {
			var user = objectMapper.readValue(message, UserTable.class);
			cartServiceImpl.saveUser(Mono.just(user)).subscribe();
		
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "user_updated-Cart")
	public void user_updated_Cart(String message) {
		try {
			var user = objectMapper.readValue(message, UserTable.class);
			cartServiceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "food_created-Cart")
	public void food_created_Cart(String message) {
		try {
			var food = objectMapper.readValue(message, FoodTable.class);
			cartServiceImpl.saveFood(Mono.just(food)).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "food_updated-Cart")
	public void food_updated_Cart(String message) {
		try {
			var food = objectMapper.readValue(message, FoodTable.class);
			cartServiceImpl.updateFood(food.getPublicId(),Mono.just(food)).subscribe();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			LOG.error("Error is {}", e.getMessage());
		}
	}
}
