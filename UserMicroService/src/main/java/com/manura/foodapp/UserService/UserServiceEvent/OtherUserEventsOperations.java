package com.manura.foodapp.UserService.UserServiceEvent;

import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OtherUserEventsOperations implements Runnable {
	
	private final OtherUserEvents otherUserEventsdata;
	private final RabbitTemplate rabbitTemplate;
	private final String action;
	private final RedissonReactiveClient client;
	
	private void event() {
		try {
			if (action == "user_verify_email") {
		        RTopicReactive topic = this.client.getTopic("user_verify_email");
		        topic.publish(otherUserEventsdata).subscribe();
			}
			if (action == "user_password_reset_success") {
				 RTopicReactive topic = this.client.getTopic("user_password_reset_success");
			     topic.publish(otherUserEventsdata).subscribe();	
			     }
			if (action == "user_security") {
				 RTopicReactive topic = this.client.getTopic("user_security");
			     topic.publish(otherUserEventsdata).subscribe();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		event();
	}
}
