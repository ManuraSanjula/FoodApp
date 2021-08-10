package com.manura.foodapp.FoodService.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;
import com.manura.foodapp.FoodService.entity.FoodEntity;
import com.manura.foodapp.FoodService.service.RedisService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RedisServiceImpl implements RedisService {
	
	@Autowired
	ReactiveRedisTemplate<String, FoodCachingRedis> reactiveRedisTemplateForFood;
	private ReactiveValueOperations<String, FoodCachingRedis> reactiveRedisTemplateOpsFood;
	
	@Autowired
	ReactiveRedisTemplate<String, CommentCachingRedis> reactiveRedisTemplateComment;
	private ReactiveValueOperations<String, CommentCachingRedis> reactiveRedisTemplateOpsComment;

	@PostConstruct
	public void setup() {
		reactiveRedisTemplateOpsFood = reactiveRedisTemplateForFood.opsForValue();
		reactiveRedisTemplateOpsComment = reactiveRedisTemplateComment.opsForValue();
	}
	
	@Override
	public void save(FoodCachingRedis obj) {
		try {
			reactiveRedisTemplateOpsFood.set(obj.getName(), obj).publishOn(Schedulers.boundedElastic())
			.subscribeOn(Schedulers.boundedElastic()).subscribe(i->{
				
			});
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public Mono<Void> ifCacheEmpty(FoodCachingRedis obj){
		try {
			save(obj);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return Mono.empty();
	}
	
	@Override
	public Mono<FoodEntity> getFood(String name) {
		try {
			return reactiveRedisTemplateOpsFood.get(name)
					.publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic())
					.map(i->i.getFood())
					.switchIfEmpty(Mono.empty());
		}catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Flux<FoodEntity> findAllComment(String foodId, CommentCachingRedis obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(CommentCachingRedis obj) {
		// TODO Auto-generated method stub
		
	}
}
