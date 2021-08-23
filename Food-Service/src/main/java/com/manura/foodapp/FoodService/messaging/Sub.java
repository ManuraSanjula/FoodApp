package com.manura.foodapp.FoodService.messaging;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodService.dto.FoodHutDtoForMessaging;
import com.manura.foodapp.FoodService.dto.FoodHutDtoForSubSaving;
import com.manura.foodapp.FoodService.dto.UserDto;
import com.manura.foodapp.FoodService.service.impl.FoodServiceImpl;

import reactor.core.publisher.Mono;

@Service
public class Sub {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private FoodServiceImpl serviceImpl;

	private ModelMapper modelMapper = new ModelMapper();

	private static final Logger LOG = LoggerFactory.getLogger(Sub.class);

	@RabbitListener(queues = "user_created-food")
	public void user_created_food(String message) {
		try {
			var user = objectMapper.readValue(message, UserDto.class);
			serviceImpl.saveUser(Mono.just(user)).subscribe();	
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "user_updated-food")
	public void user_updated_food(String message) {
		try {
			var user = objectMapper.readValue(message, UserDto.class);
			serviceImpl.updateUser(user.getPublicId(),Mono.just(user)).subscribe(i->{
			});
		} catch (Exception e) {
			LOG.info("Error is {}", e.getMessage());
		}
	}
	
	@RabbitListener(queues = "foodHut_created-food")
	public void foodHut_created_food(String message) {
		try {
			var foodHut = objectMapper.readValue(message, FoodHutDtoForMessaging.class);
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
	
	@RabbitListener(queues = "foodHut_updated-food")
	public void foodHut_updated_food(String message) {
		try {
			var foodHut = objectMapper.readValue(message, FoodHutDtoForMessaging.class);
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
