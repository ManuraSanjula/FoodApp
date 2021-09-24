package com.manura.foodapp.CartService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.manura.foodapp.CartService.Dto.RedisCartDto;


@Configuration
public class RedisConfig {
	@Bean
	public ReactiveRedisTemplate<String, RedisCartDto> reactiveRedisTemplateForCart(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<RedisCartDto> valueSerializer = new Jackson2JsonRedisSerializer<>(RedisCartDto.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, RedisCartDto> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, RedisCartDto> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
}
