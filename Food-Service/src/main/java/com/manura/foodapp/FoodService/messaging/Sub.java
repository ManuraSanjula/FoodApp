package com.manura.foodapp.FoodService.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodService.dto.UserDto;
import com.manura.foodapp.FoodService.service.impl.FoodServiceImpl;

import reactor.core.publisher.Mono;

@Service
public class Sub {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private FoodServiceImpl serviceImpl;

	private static final Logger LOG = LoggerFactory.getLogger(Sub.class);

	@RabbitListener(queues = "user_created-food")
	public void user_created_food(String message) {
		try {
			var user = objectMapper.readValue(message, UserDto.class);
			serviceImpl.saveUser(Mono.just(user)).subscribe();
			LOG.info("Employee is {}", user);
		} catch (Exception e) {
			
		}
	}
	
	@RabbitListener(queues = "user_updated-food")
	public void user_updated(String message) {
		try {
			var user = objectMapper.readValue(message, UserDto.class);
			serviceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe();
			LOG.info("Employee is {}", user);
		} catch (Exception e) {
			
		}
	}
}
