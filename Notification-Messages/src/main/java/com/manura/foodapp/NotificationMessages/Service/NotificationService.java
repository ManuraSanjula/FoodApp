package com.manura.foodapp.NotificationMessages.Service;

import org.redisson.Redisson;
import org.redisson.api.RSetReactive;
import org.redisson.api.RTopic;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.NotificationMessages.model.OtherUserEvents;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@Service
public class NotificationService implements WebSocketHandler {

	@Autowired
	private RedissonReactiveClient client;


	@Override
	public Mono<Void> handle(WebSocketSession webSocketSession) {
		String room = getChatRoomName(webSocketSession);
		if (room == null) { 
			Flux<WebSocketMessage> flux = Flux.empty();
			return webSocketSession.send(flux);
		}
		RTopicReactive topic = client.getTopic(room, StringCodec.INSTANCE);
		RSetReactive<String> list = client.getSet("notification:" + room);
	
//		RTopic user_topic1 = getClient().getTopic("user_verify_email");
//		user_topic1.addListener(String.class, new MessageListener<String>() {
//			@Override
//			public void onMessage(CharSequence channel, String msg) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				try {
//					OtherUserEvents userEvents = objectMapper.readValue(msg, OtherUserEvents.class);
//					if (userEvents.getUser().equals(room)) {
//						list.add(msg).then(topic.publish(msg)).subscribe(i -> {
//							System.out.println(msg);
//						});
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		RTopic user_topic2 = getClient().getTopic("user_password_reset_success");
//		user_topic2.addListener(String.class, new MessageListener<String>() {
//			@Override
//			public void onMessage(CharSequence channel, String msg) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				try {
//					OtherUserEvents userEvents = objectMapper.readValue(msg, OtherUserEvents.class);
//					if (userEvents.getUser().equals(room)) {
//						list.add(msg).then(topic.publish(msg)).subscribe(i -> {
//							System.out.println(msg);
//						});
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
		Flux<WebSocketMessage> flatMap = list.readAll().map(i->{
			return topic.getMessages(String.class).doOnNext(msg->System.out.println(msg))
			.startWith(Flux.fromIterable(i))
			.doOnNext(msg->System.out.println(msg))
			.map(webSocketSession::textMessage).doOnError(System.out::println);
		}).flux().flatMap(__->__);
		
//		Flux<WebSocketMessage> flux = topic.getMessages(String.class).doOnNext(msg->System.out.println(msg))
//				.startWith(Flux.fromIterable(list.readAll().block()))
//				.doOnNext(msg->System.out.println(msg))
//				.map(webSocketSession::textMessage).doOnError(System.out::println);

		return webSocketSession.send(flatMap);
	}

	private String getChatRoomName(WebSocketSession socketSession) {
		URI uri = socketSession.getHandshakeInfo().getUri();
		return UriComponentsBuilder.fromUri(uri).build().getQueryParams().toSingleValueMap().get("user");
	}

}
