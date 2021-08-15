package com.manura.foodapp.FoodService.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodService.entity.FoodEntity;

@Service
public class Pub {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void pubFood(Mono<FoodEntity> food, String action) {
        food.subscribe(data -> {
            try {
                if (action == "created") {
                	data.setPublicId(data.getPublicId());
                	data.setId(null);
                    var json = objectMapper.writeValueAsString(data);

                    rabbitTemplate.convertAndSend("food-app-foodCreated", "", json);
                }
                if (action == "update") {
                	data.setPublicId(data.getPublicId());
                	data.setId(null);
                    var json = objectMapper.writeValueAsString(data);

                    rabbitTemplate.convertAndSend("food-app-foodUpdated", "", json);
                }
            } catch (Exception e) {

            }
        });
    }
}
