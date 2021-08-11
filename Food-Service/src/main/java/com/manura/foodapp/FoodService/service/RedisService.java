package com.manura.foodapp.FoodService.service;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodCommentDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;
import com.manura.foodapp.FoodService.dto.UserCommentDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RedisService {
	public void save(FoodCachingRedis obj);
	public Mono<FoodDto> getFood(String name);
	Flux<CommentsDto> findAllComment(String id);
	public void save(CommentCachingRedis obj);
	public Mono<Void> updateCommentIFFoodUpdated(String key,FoodCommentDto food);
	public Mono<Void> updateFoodIFFoodHutUpdated(String key,String foodHutId,FoodHutDto food);
	public Mono<Void> updateCommentIFUserUpdated(String key,UserCommentDto user);
	public Mono<Void> addNewComment(String key,CommentsDto comment);
}
