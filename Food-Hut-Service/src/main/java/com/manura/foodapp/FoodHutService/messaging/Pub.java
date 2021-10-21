package com.manura.foodapp.FoodHutService.messaging;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodHutService.Node.FoodHutNode;
import com.manura.foodapp.FoodHutService.dto.FoodHutDtoForMessaging;

import reactor.core.publisher.Mono;

@Service
public class Pub {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
    private ModelMapper modelMapper = new ModelMapper();
    
    public void pubFood(Mono<FoodHutNode> food, String action) {
        food.subscribe(data -> {
        	FoodHutDtoForMessaging foodHutDtoForMessaging = modelMapper.map(data, FoodHutDtoForMessaging.class);
        	Double latitude = data.getLocation().getY();
			Double longitude = data.getLocation().getX();
			foodHutDtoForMessaging.setLatitude(latitude);
			foodHutDtoForMessaging.setLongitude(longitude);
            try {
                if (action == "created") {
                    String json = objectMapper.writeValueAsString(foodHutDtoForMessaging);

                    rabbitTemplate.convertAndSend("food-app-foodHutCreated", "", json);
                }
                if (action == "update") {
                	String json = objectMapper.writeValueAsString(foodHutDtoForMessaging);

                    rabbitTemplate.convertAndSend("food-app-foodHutUpdated", "", json);
                }
            } catch (Exception e) {
                
            }
        });
    }

}
