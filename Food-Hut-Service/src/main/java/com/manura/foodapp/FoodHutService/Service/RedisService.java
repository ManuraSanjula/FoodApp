package com.manura.foodapp.FoodHutService.Service;

import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.Redis.model.CommentCachingRedis;
import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RedisService {
	
	public void save(FoodHutDto obj);
	public void saveComment(String id,CommentCachingRedis obj);
	
	public Mono<Void> addNewUser(UserNode user);
	public Mono<Void> updateUser(String key,UserNode user);
	public Mono<UserNode> getUser(String key);
	
	public Mono<FoodHutDto> getOneFoodHut(String key);
	public Flux<CommentsDto> getAllComments(String key);
	
	public Mono<Void> commentUpdated(String foodId,String commentId,CommentsDto commentDto);
	public Mono<Void> deleteComment(String foodId,String commentId);
	public Mono<Void> addNewComment(String key,CommentsDto comment);
	
}
