package com.manura.foodapp.FoodHutService.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.FoodHutService.Node.FoodHutNode;

import reactor.core.publisher.Mono;

@Service
public class Pub {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
    
    public void pubFood(Mono<FoodHutNode> food, String action) {
        food.subscribe(data -> {
            try {
                if (action == "created") {
                    var json = objectMapper.writeValueAsString(data);

                    rabbitTemplate.convertAndSend("food-app-foodHutCreated", "", json);
                }
                if (action == "update") {
                    var json = objectMapper.writeValueAsString(data);

                    rabbitTemplate.convertAndSend("food-app-foodHutUpdated", "", json);
                }
            } catch (Exception e) {

            }
        });
    }

}
