package com.manura.foodapp.UserService.UserServiceEvent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.UserService.shared.DTO.UserDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

public class UserAccountEvent implements Runnable {

    private final UserDto createdUser;
    private final RabbitTemplate rabbitTemplate;
    private final String action;

    public UserAccountEvent(UserDto createdUser, RabbitTemplate rabbitTemplate, String action) {
        this.rabbitTemplate = rabbitTemplate;
        this.createdUser = createdUser;
        this.action = action;
    }

    private void event() throws IOException, InterruptedException {
       if(action == "userCreated"){
           ObjectMapper oMapper = new ObjectMapper();
           String json = oMapper.writeValueAsString(createdUser);
           rabbitTemplate.convertAndSend("food-app-userCreated", "" ,json);
       }
       if(action == "userUpdated"){
           ObjectMapper oMapper = new ObjectMapper();
           String json = oMapper.writeValueAsString(createdUser);
           rabbitTemplate.convertAndSend("food-app-userUpdated", "" ,json);
       }
    }

    @Override
    public void run() {
        try {
            event();
        } catch (InterruptedException e) {
            
        } catch (IOException e) {
           
        }
    }
}
