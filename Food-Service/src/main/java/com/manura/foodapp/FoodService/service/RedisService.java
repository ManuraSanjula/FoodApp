package com.manura.foodapp.FoodService.service;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RedisService {
	public void save(FoodCachingRedis obj);
	public Mono<FoodDto> getFood(String name);
	Flux<CommentsDto> findAllComment(String id);
	public void save(CommentCachingRedis obj);
}
