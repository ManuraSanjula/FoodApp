package com.manura.foodapp.NotificationMessages;

import org.redisson.Redisson;
import org.redisson.api.RSetReactive;
import org.redisson.api.RTopic;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.NotificationMessages.model.OtherUserEvents;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class NotificationMessagesApplication {

	public static void main(String[] args) {
		
		RedissonClient redisson;
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		redisson = Redisson.create(config);
		
		SpringApplication.run(NotificationMessagesApplication.class, args);
		RTopic user_topic1 = redisson.getTopic("user_verify_email");
		user_topic1.addListener(String.class, new MessageListener<String>() {

			@Override
			public void onMessage(CharSequence channel, String msg) {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					OtherUserEvents userEvents = objectMapper.readValue(msg,OtherUserEvents.class);
					RTopicReactive topic = redisson.reactive().getTopic(userEvents.getUser(), StringCodec.INSTANCE);
					RSetReactive<String> list = redisson.reactive().getSet("notification:" + userEvents.getUser());
					list.add(msg).subscribe(i->{
						topic.publish(msg).subscribe(h->{
							System.out.println(msg);
						});
						System.out.println(msg);
					});
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		  
		});
		RTopic user_topic2 = redisson.getTopic("user_password_reset_success");
		user_topic2.addListener(String.class, new MessageListener<String>() {
			@Override
			public void onMessage(CharSequence channel, String msg) {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					OtherUserEvents userEvents = objectMapper.readValue(msg,OtherUserEvents.class);
					RSetReactive<String> list = redisson.reactive().getSet("notification:" + userEvents.getUser());
					list.add(msg).subscribe(i->{
						System.out.println(msg);
					});
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		  
		});
	}

}
