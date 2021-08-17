package com.manura.foodapp.FoodService.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.Redis.Model.FoodCachingRedis;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodCommentDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;
import com.manura.foodapp.FoodService.dto.UserCommentDto;
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
				if(i.getComment() != null) {
					return Flux.fromIterable(i.getComment());
				}else {
					return Flux.fromIterable(new ArrayList<>());
				}
			}).flatMapMany(i -> i).switchIfEmpty(Flux.fromIterable(new ArrayList<>())).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).mapNotNull(i -> modelMapper.map(i, CommentsDto.class));

		} catch (Exception e) {
			return Flux.fromIterable(new ArrayList<>());
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

	@Override
	public Mono<Void> updateCommentIFFoodUpdated(String key,FoodCommentDto food) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + key).subscribe(i -> {
				List<CommentsDto> comment = new ArrayList<>();
				comment.addAll(i.getComment());
				comment.forEach(comm -> {
					if (comm.getFood().getId().equals(food.getId())) {
						comm.setFood(food);
					}
				});
				reactiveRedisTemplateOpsComment.set("Comment" + key, i).subscribe(__->{
					
				});
			});
		}catch (Exception e) {
		}
		return Mono.empty();
	}

	@Override
	public Mono<Void> updateFoodIFFoodHutUpdated(String key, String foodHutId,FoodHutDto food) {
		try {
			reactiveRedisTemplateOpsFood.get(key).publishOn(Schedulers.boundedElastic())
			  .subscribeOn(Schedulers.boundedElastic()).subscribe(i->{
				 i.getFood().getFoodHuts().forEach(foodData->{
					 if(foodData.getId().equals(foodHutId)) {
						 foodData = food;
					 }
				 });
			  });
		}catch (Exception e) {
			
		}
		return Mono.empty();
	}

	@Override
	public Mono<Void> updateCommentIFUserUpdated(String key, UserCommentDto user) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + key).subscribe(i -> {
				i.getComment().forEach(comm -> {
					if(comm.getUser().getEmail().equals(user.getEmail())) {
						comm.setUser(user);
					}
				});
				reactiveRedisTemplateOpsComment.set("Comment" + key, i).subscribe(__->{
					
				});
			});
		}catch (Exception e) {
		}
		return Mono.empty();
	}

	@Override
	public Mono<Void> addNewComment(String key, CommentsDto comment) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + key).subscribe(i->{
				List<CommentsDto> commentList = new ArrayList<>();
				commentList.addAll(i.getComment());
				commentList.add(comment);
				i.setComment(commentList);
				reactiveRedisTemplateOpsComment.set("Comment" + key, i).subscribe(__->{
					
				});
			});
		}catch (Exception e) {
		}
		return Mono.empty();
	}

	@Override
	public Mono<Void> commentUpdated(String foodId,String commentId, CommentsDto commentDto) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + foodId).subscribe(i -> {
				List<CommentsDto> comment = new ArrayList<>();
				comment.addAll(i.getComment());
				comment.forEach(commentData->{
					if(commentData.getId().equals(commentDto.getId())) {
						commentData.setDescription(commentDto.getDescription());
					}
				});
				i.setComment(comment);
				try {
					reactiveRedisTemplateOpsComment.set("Comment" + foodId, i).subscribe(__->{
						
					});
				}catch (Exception e) {
					// TODO: handle exception
				}
			});
			return Mono.empty();
		}catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<Void> deleteComment(String foodId, String commentId) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + foodId).subscribe(i -> {
				List<CommentsDto> comment = new ArrayList<>();
				comment.addAll(i.getComment());
				comment.removeIf(j->j.getId().equals(commentId));
				i.setComment(comment);
				try {
					reactiveRedisTemplateOpsComment.set("Comment" + foodId, i).subscribe(d->{
						
					});
				}catch (Exception e) {
					// TODO: handle exception
				}
			});
			return Mono.empty();
		}catch (Exception e) {
			return Mono.empty();
		}
	}


}