package com.manura.foodapp.UserService.UserServiceEvent;

import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
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

	private RedissonClient getClient() {
		RedissonClient redissonClient;
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		redissonClient = Redisson.create(config);
		return redissonClient;
	}

	private void event() {
		try {
			if (action == "user_verify_email") {
				ObjectMapper oMapper = new ObjectMapper();
				RTopic topic = getClient().getTopic("user_verify_email");
				String json = oMapper.writeValueAsString(otherUserEventsdata);
				topic.publish(json);
			}
			if (action == "user_password_reset_success") {
				ObjectMapper oMapper = new ObjectMapper();
				RTopic topic = getClient().getTopic("user_password_reset_success");
				String json = oMapper.writeValueAsString(otherUserEventsdata);
				topic.publish(json);
			}
			if (action == "user_security") {
				ObjectMapper oMapper = new ObjectMapper();
				RTopic topic = getClient().getTopic("user_security");
				topic.publish(otherUserEventsdata);
				String json = oMapper.writeValueAsString(otherUserEventsdata);
				topic.publish(json);
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	public void run() {
		event();
	}
}
