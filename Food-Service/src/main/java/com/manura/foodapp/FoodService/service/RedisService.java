package com.manura.foodapp.FoodService.service;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;
import com.manura.foodapp.FoodService.entity.FoodEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RedisService {
	public void save(FoodCachingRedis obj);
	public Mono<Void> ifCacheEmpty(FoodCachingRedis obj);
	public Mono<FoodEntity> getFood(String name);
	Flux<FoodEntity> findAllComment(String foodId,CommentCachingRedis obj);
	public void save(CommentCachingRedis obj);
}
