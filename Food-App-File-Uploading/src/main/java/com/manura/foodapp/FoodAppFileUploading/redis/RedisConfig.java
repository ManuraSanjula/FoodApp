package com.manura.foodapp.FoodAppFileUploading.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.manura.foodapp.FoodAppFileUploading.Redis.ImageCachingRedis;

@Configuration
public class RedisConfig {
	@Bean
	public ReactiveRedisTemplate<String, ImageCachingRedis> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<ImageCachingRedis> valueSerializer = new Jackson2JsonRedisSerializer<>(ImageCachingRedis.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, ImageCachingRedis> builder = RedisSerializationContext
				.newSerializationContext(keySerializer);
		RedisSerializationContext<String, ImageCachingRedis> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}
}
