package com.manura.foodapp.FoodHutService.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodHutService.Node.FoodNode;
import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.Service.Impl.FoodHutServiceImpl;

import reactor.core.publisher.Mono;

@Service
public class Sub {
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private FoodHutServiceImpl foodHutServiceImpl;
	
	private static final Logger LOG = LoggerFactory.getLogger(Sub.class);
	
	@RabbitListener(queues = "user_created-foodHut",concurrency = "20")
	public void user_created_foodHut(String message) {
		try {
			UserNode user = objectMapper.readValue(message, UserNode.class);
			foodHutServiceImpl.addUser(Mono.just(user)).subscribe();
		
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	@RabbitListener(queues = "user_security-foodHut",concurrency = "20")
	public void user_security_foodHut(String message) {
		try {
			UserNode user = objectMapper.readValue(message, UserNode.class);
			foodHutServiceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe();		
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "user_updated-foodHut",concurrency = "20")
	public void user_updated_foodHut(String message) {
		try {
			UserNode user = objectMapper.readValue(message, UserNode.class);
			foodHutServiceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe();
			
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "food_created-foodHut",concurrency = "20")
	public void food_created_foodHut(String message) {
		try {
			FoodNode food = objectMapper.readValue(message, FoodNode.class);
			foodHutServiceImpl.addFood(Mono.just(food)).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "food_updated-foodHut",concurrency = "20")
	public void food_updated_foodHut(String message) {
		try {
			FoodNode food = objectMapper.readValue(message, FoodNode.class);
			foodHutServiceImpl.updateFood(food.getPublicId(),Mono.just(food)).subscribe();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			LOG.error("Error is {}", e.getMessage());
		}
	}
}