package com.manura.foodapp.FoodService.messaging;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodService.dto.FoodDtoForMessaging;
import com.manura.foodapp.FoodService.entity.FoodEntity;

@Service
public class Pub {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
	private ModelMapper modelMapper = new ModelMapper();

    public void pubFood(Mono<FoodEntity> food, String action) {
        food.subscribe(data -> {
        	FoodDtoForMessaging foodDtoForMessaging = modelMapper.map(data, FoodDtoForMessaging.class);
        	foodDtoForMessaging.setPublicId(data.getId());
            try {
                if (action == "created") {
                    var json = objectMapper.writeValueAsString(foodDtoForMessaging);
                    rabbitTemplate.convertAndSend("food-app-foodCreated", "", json);
                }
                if (action == "update") {
                    var json = objectMapper.writeValueAsString(foodDtoForMessaging);
                    rabbitTemplate.convertAndSend("food-app-foodUpdated", "", json);
                }
            } catch (Exception e) {

            }
        });
    }
}
