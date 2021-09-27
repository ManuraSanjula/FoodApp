package com.manura.foodapp.NotificationMessages.Service;

import java.net.URI;

import org.redisson.api.RSetReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
	
		Flux<WebSocketMessage> flatMap = list.readAll().map(i->{
			return topic.getMessages(String.class).doOnNext(msg->System.out.println(msg))
			.startWith(Flux.fromIterable(i))
			.map(webSocketSession::textMessage).doOnError(System.out::println);
		}).flux().flatMap(__->__);

		return webSocketSession.send(flatMap);
	}

	private String getChatRoomName(WebSocketSession socketSession) {
		URI uri = socketSession.getHandshakeInfo().getUri();
		return UriComponentsBuilder.fromUri(uri).build().getQueryParams().toSingleValueMap().get("user");
	}

}
