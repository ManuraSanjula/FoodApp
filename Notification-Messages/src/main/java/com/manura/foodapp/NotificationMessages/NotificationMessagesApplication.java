package com.manura.foodapp.NotificationMessages;

import org.redisson.api.RSetReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.NotificationMessages.model.OtherUserEvents;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class NotificationMessagesApplication implements CommandLineRunner {
	
	@Autowired
	private RedissonReactiveClient client;

	public static void main(String[] args) {
		SpringApplication.run(NotificationMessagesApplication.class, args);

	}
    private void publish(String msg) {
    	ObjectMapper objectMapper = new ObjectMapper();
		try {
			OtherUserEvents userEvents = objectMapper.readValue(msg,OtherUserEvents.class);
			RTopicReactive topic = client.getTopic(userEvents.getUser(), StringCodec.INSTANCE);
			RSetReactive<String> list = client.getSet("notification:" + userEvents.getUser());
			list.add(msg).subscribe(i->{
				topic.publish(msg).subscribe(h->{
					
				});
			});
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
	@Override
	public void run(String... args) throws Exception {
		RTopicReactive user_verify_email = client.getTopic("user_verify_email");
		user_verify_email.getMessages(String.class).subscribe(msg->{
			publish(msg);
		});
		RTopicReactive user_password_reset_success = client.getTopic("user_password_reset_success");
		user_password_reset_success.getMessages(String.class).subscribe(msg->{
			publish(msg);
		});
	}

}
