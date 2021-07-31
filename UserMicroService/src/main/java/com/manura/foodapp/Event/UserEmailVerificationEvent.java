package com.manura.foodapp.Event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserEmailVerificationEvent implements Runnable {

    RabbitTemplate rabbitTemplate;

    @Override
    public void run() {
        rabbitTemplate.convertAndSend("foods_ex_app", "uu", "true");
    }

}
