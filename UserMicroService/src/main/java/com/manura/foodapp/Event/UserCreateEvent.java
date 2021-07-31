package com.manura.foodapp.Event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.shared.DTO.UserDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.io.IOException;

public class UserCreateEvent implements Runnable {
    
    UserDto createdUser;
    RabbitTemplate rabbitTemplate;

    public UserCreateEvent() {

    }

    public UserCreateEvent(UserDto createdUser, RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.createdUser = createdUser;
    }

    private void event() throws IOException, InterruptedException {

        ObjectMapper oMapper = new ObjectMapper();
        String json = oMapper.writeValueAsString(createdUser);
        rabbitTemplate.convertAndSend("foods_ex_app", "uc", json);

        /*
         * Connection natsConnection = Nats.connect(); json =
         * oMapper.writeValueAsString(createdUser);
         * natsConnection.publish("user_created", json.getBytes())
         */
        ;

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
