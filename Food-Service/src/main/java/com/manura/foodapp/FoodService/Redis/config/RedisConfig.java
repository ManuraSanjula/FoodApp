package com.manura.foodapp.FoodService.Redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.entity.UserEntity;

@Configuration
public class RedisConfig {
	@Bean
	public ReactiveRedisTemplate<String, FoodDto> reactiveRedisTemplateForFood(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<FoodDto> valueSerializer = new Jackson2JsonRedisSerializer<>(FoodDto.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, FoodDto> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, FoodDto> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
	
	@Bean
	public ReactiveRedisTemplate<String, CommentCachingRedis> reactiveRedisTemplateForComment(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<CommentCachingRedis> valueSerializer = new Jackson2JsonRedisSerializer<>(CommentCachingRedis.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, CommentCachingRedis> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, CommentCachingRedis> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
	
	@Bean
	public ReactiveRedisTemplate<String, UserEntity> reactiveRedisTemplateForUser(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<UserEntity> valueSerializer = new Jackson2JsonRedisSerializer<>(UserEntity.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, UserEntity> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, UserEntity> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
}