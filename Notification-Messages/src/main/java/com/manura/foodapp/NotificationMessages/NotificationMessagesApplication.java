package com.manura.foodapp.NotificationMessages;

import org.redisson.Redisson;
import org.redisson.api.RListReactive;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.NotificationMessages.model.OtherUserEvents;

@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
public class NotificationMessagesApplication {

	public static void main(String[] args) {
		
		RedissonClient redisson;
		Config config = new Config();
		config.useSingleServer().setAddress("redis://127.0.0.1:6379");
		redisson = Redisson.create(config);
		
		SpringApplication.run(NotificationMessagesApplication.class, args);
		RTopic topic = redisson.getTopic("user_verify_email");
		
		topic.addListener(String.class, new MessageListener<String>() {

			@Override
			public void onMessage(CharSequence channel, String msg) {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					OtherUserEvents userEvents = objectMapper.readValue(msg,OtherUserEvents.class);
					RListReactive<OtherUserEvents> list = redisson.reactive().getList("notification:" + userEvents.getUser());
					list.add(userEvents).subscribe(i->{
						System.out.println(i);
					});
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		  
		});
	}

}
