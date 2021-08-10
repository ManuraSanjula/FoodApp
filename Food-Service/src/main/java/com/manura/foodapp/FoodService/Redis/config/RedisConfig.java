package com.manura.foodapp.FoodService.Redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;

@Configuration
public class RedisConfig {
	@Bean
	public ReactiveRedisTemplate<String, FoodCachingRedis> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<FoodCachingRedis> valueSerializer = new Jackson2JsonRedisSerializer<>(FoodCachingRedis.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, FoodCachingRedis> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, FoodCachingRedis> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
}