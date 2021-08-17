package com.manura.foodapp.FoodHutService.RSocket.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.cbor.Jackson2CborEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

import io.rsocket.transport.netty.client.TcpClientTransport;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Configuration
public class RSocketConfig {
	@Value("${food.service.port}")
	int port;

	@Bean
	public RSocketStrategies rSocketStrategies() {
		return RSocketStrategies.builder().encoders(encoders -> encoders.add(new Jackson2CborEncoder()))
				.decoders(decoders -> decoders.add(new Jackson2CborDecoder())).build();
	}

	@Bean
	public Mono<RSocketRequester> getRSocketRequester(RSocketRequester.Builder builder) {
		return builder
				.rsocketConnector(
						rSocketConnector -> rSocketConnector.reconnect(retryStrategy()))
				.connect(TcpClientTransport.create(port));
	}
	
	private Retry retryStrategy(){
        return Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
                    .doBeforeRetry(s -> System.out.println("Lost connection. Retry : " + s.totalRetriesInARow()));
    }

}