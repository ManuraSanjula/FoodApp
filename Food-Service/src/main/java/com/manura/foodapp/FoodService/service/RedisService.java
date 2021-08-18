package com.manura.foodapp.FoodService.service;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodCommentDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;
import com.manura.foodapp.FoodService.dto.UserCommentDto;
import com.manura.foodapp.FoodService.entity.UserEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RedisService {
	public void save(FoodCachingRedis obj);
	public void saveComment(CommentCachingRedis obj);

	public Mono<Void> addNewUser(UserEntity user);
	public Mono<Void> updateUser(String key,UserEntity user);
	public Mono<FoodDto> getFood(String name);
	public Flux<CommentsDto> findAllComment(String id);
	public Mono<UserEntity> getUser(String user);
	
	public Mono<Void> updateCommentIFFoodUpdated(String key,FoodCommentDto food);
	public Mono<Void> updateCommentIFUserUpdated(String key,UserCommentDto user);
	
	public Mono<Void> commentUpdated(String foodId,String commentId,CommentsDto commentDto);
	public Mono<Void> deleteComment(String foodId,String commentId);
	public Mono<Void> addNewComment(String key,CommentsDto comment);
	
	public Mono<Void> updateFoodIFFoodHutUpdated(String key,String foodHutId,FoodHutDto food);
	
}
