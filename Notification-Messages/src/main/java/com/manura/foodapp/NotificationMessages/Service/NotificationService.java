package com.manura.foodapp.NotificationMessages.Service;

import org.redisson.Redisson;
import org.redisson.api.RListReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.NotificationMessages.model.OtherUserEvents;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;

@Service
public class NotificationService implements WebSocketHandler {

	private RedissonClient redissonClient;

	private RedissonClient getClient() {
		if (Objects.isNull(this.redissonClient)) {
			Config config = new Config();
			config.useSingleServer().setAddress("redis://127.0.0.1:6379");
			redissonClient = Redisson.create(config);
		}
		return redissonClient;
	}

	private RedissonReactiveClient getReactiveClient() {
		return getClient().reactive();
	}

	@Override
	public Mono<Void> handle(WebSocketSession webSocketSession) {
		RedissonReactiveClient client = getReactiveClient();
		String room = getChatRoomName(webSocketSession);
		if (room.isEmpty()) {
			Flux<WebSocketMessage> flux = Flux.empty();
			return webSocketSession.send(flux);
		}
		RListReactive<OtherUserEvents> list = client.getList("notification:" + room);
		RTopicReactive user_topic = client.getTopic(room);

		
		

		return webSocketSession.send(Flux.empty());
	}

	private String getChatRoomName(WebSocketSession socketSession) {
		URI uri = socketSession.getHandshakeInfo().getUri();
		return UriComponentsBuilder.fromUri(uri).build().getQueryParams().toSingleValueMap().getOrDefault("user", "");
	}

}
