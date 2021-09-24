package com.manura.foodapp.FoodHutService.Redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.Redis.model.CommentCachingRedis;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;


@Configuration
public class RedisConfig {
	@Bean
	public ReactiveRedisTemplate<String, FoodHutDto> reactiveRedisTemplateForFood(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<FoodHutDto> valueSerializer = new Jackson2JsonRedisSerializer<>(FoodHutDto.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, FoodHutDto> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, FoodHutDto> context = builder.value(valueSerializer).build();
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
	public ReactiveRedisTemplate<String, UserNode> reactiveRedisTemplateForUser(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<UserNode> valueSerializer = new Jackson2JsonRedisSerializer<>(UserNode.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, UserNode> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, UserNode> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
}