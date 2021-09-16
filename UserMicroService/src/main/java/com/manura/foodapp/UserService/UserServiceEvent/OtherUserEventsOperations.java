package com.manura.foodapp.UserService.UserServiceEvent;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OtherUserEventsOperations implements Runnable {
	private final OtherUserEvents otherUserEventsdata;
	private final RabbitTemplate rabbitTemplate;
	private final String action;

	private void event() {
		try {
			if (action == "user_verify_email") {
				ObjectMapper oMapper = new ObjectMapper();
				String json = oMapper.writeValueAsString(otherUserEventsdata);
				rabbitTemplate.convertAndSend("user_verify_email", "", json);
			}
			if (action == "user_password_reset_success") {
				ObjectMapper oMapper = new ObjectMapper();
				String json = oMapper.writeValueAsString(otherUserEventsdata);
				rabbitTemplate.convertAndSend("user_password_reset_success", "", json);
			}
			if (action == "user_security") {
				ObjectMapper oMapper = new ObjectMapper();
				String json = oMapper.writeValueAsString(otherUserEventsdata);
				rabbitTemplate.convertAndSend("user_security", "", json);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		event();
	}
}
