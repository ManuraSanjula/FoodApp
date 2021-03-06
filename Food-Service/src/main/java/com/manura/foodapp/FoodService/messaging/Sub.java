package com.manura.foodapp.FoodService.messaging;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodService.dto.FoodHutDtoForMessaging;
import com.manura.foodapp.FoodService.dto.FoodHutDtoForSubSaving;
import com.manura.foodapp.FoodService.dto.UserDto;
import com.manura.foodapp.FoodService.entity.UserEntity;
import com.manura.foodapp.FoodService.service.impl.FoodServiceImpl;

import reactor.core.publisher.Mono;

@Service
public class Sub {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private FoodServiceImpl serviceImpl;

	private ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
	private ReactiveRedisTemplate<String, UserEntity> reactiveRedisTemplateUser;
	private ReactiveValueOperations<String, UserEntity> reactiveRedisTemplateOpsUser;

	@PostConstruct
	public void setup() {
		reactiveRedisTemplateOpsUser = reactiveRedisTemplateUser.opsForValue();
	}
	@RabbitListener(queues = "user_security-food",concurrency = "20")
    public void user_security_food(String message) {
		try {
			UserDto user = objectMapper.readValue(message, UserDto.class);
			serviceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe(i->{
			});
			reactiveRedisTemplateOpsUser.delete(user.getEmail()).subscribe(i->{});
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
    }
	private static final Logger LOG = LoggerFactory.getLogger(Sub.class);

	@RabbitListener(queues = "user_created-food",concurrency = "20")
	public void user_created_food(String message) {
		try {
			UserDto user = objectMapper.readValue(message, UserDto.class);
			serviceImpl.saveUser(Mono.just(user)).subscribe();	
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "user_updated-food",concurrency = "20")
	public void user_updated_food(String message) {
		try {
			UserDto user = objectMapper.readValue(message, UserDto.class);
			serviceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe(i->{
			});
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "foodHut_created-food",concurrency = "20")
	public void foodHut_created_food(String message) {
		try {
			FoodHutDtoForMessaging foodHut = objectMapper.readValue(message, FoodHutDtoForMessaging.class);
			Double latitude = foodHut.getLatitude();
			Double longitude = foodHut.getLongitude();
			GeoJsonPoint locationPoint = new GeoJsonPoint(longitude,latitude);
			
			FoodHutDtoForSubSaving foodHutEntity = modelMapper.map(foodHut, FoodHutDtoForSubSaving.class);
			foodHutEntity.setLocation(locationPoint);
			serviceImpl.saveFoodHut(Mono.just(foodHutEntity)).subscribe();
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "foodHut_updated-food",concurrency = "20")
	public void foodHut_updated_food(String message) {
		try {
			FoodHutDtoForMessaging foodHut = objectMapper.readValue(message, FoodHutDtoForMessaging.class);
			Double latitude = foodHut.getLatitude();
			Double longitude = foodHut.getLongitude();
			GeoJsonPoint locationPoint = new GeoJsonPoint(longitude,latitude);
			
			FoodHutDtoForSubSaving foodHutEntity = modelMapper.map(foodHut, FoodHutDtoForSubSaving.class);
			foodHutEntity.setLocation(locationPoint);
			serviceImpl.updateFoodHut(Mono.just(foodHutEntity),foodHutEntity.getId()).subscribe();
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
}
