package com.manura.foodapp.OrderService.Redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.manura.foodapp.OrderService.Redis.Model.PdfRedis;

@Configuration
public class RedisConfig {
	@Bean
	public ReactiveRedisTemplate<String, PdfRedis> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<PdfRedis> valueSerializer = new Jackson2JsonRedisSerializer<>(PdfRedis.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, PdfRedis> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, PdfRedis> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
}
