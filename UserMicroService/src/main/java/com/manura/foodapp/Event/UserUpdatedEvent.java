package com.manura.foodapp.Event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.shared.DTO.UserDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.io.IOException;

public class UserUpdatedEvent implements Runnable {

    UserDto createdUser;
    RabbitTemplate rabbitTemplate;

    public UserUpdatedEvent() {

    }

    public UserUpdatedEvent(UserDto createdUser, RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.createdUser = createdUser;
    }

    private void event() throws IOException, InterruptedException {

        ObjectMapper oMapper = new ObjectMapper();
        String json = oMapper.writeValueAsString(createdUser);
        rabbitTemplate.convertAndSend("foods_ex_app", "uu", json);

    }

    @Override
    public void run() {
        try {
            event();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
