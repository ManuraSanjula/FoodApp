package com.manura.foodapp.FoodService.service.impl;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.service.RedisService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RedisServiceImpl implements RedisService {

	private ModelMapper modelMapper = new ModelMapper();

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
					.subscribeOn(Schedulers.boundedElastic()).subscribe(i -> {

					});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public Mono<FoodDto> getFood(String name) {
		try {
			return reactiveRedisTemplateOpsFood.get(name).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> i.getFood())
					.map(i -> modelMapper.map(i, FoodDto.class)).switchIfEmpty(Mono.empty());
		} catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Flux<CommentsDto> findAllComment(String foodId) {
		try {
			return reactiveRedisTemplateOpsComment.get("Comment" + foodId).map(i -> {
				return Flux.fromIterable(i.getComment());
			}).flatMapMany(i -> i).switchIfEmpty(Flux.empty()).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, CommentsDto.class));

		} catch (Exception e) {
			return Flux.empty();
		}
	}

	@Override
	public void save(CommentCachingRedis obj) {
		try {
			reactiveRedisTemplateOpsComment.set(obj.getName(), obj).subscribe(i -> {

			});
		} catch (Exception e) {

		}
	}
}
