package com.manura.foodapp.FoodHutService.Service.Impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.Redis.model.CommentCachingRedis;
import com.manura.foodapp.FoodHutService.Service.RedisService;
import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RedisServiceImpl implements RedisService {
	@Autowired
	private ReactiveRedisTemplate<String, FoodHutDto> reactiveRedisTemplateForFoodHut;
	private ReactiveValueOperations<String, FoodHutDto> reactiveRedisTemplateOpsFoodHut;

	@Autowired
	private ReactiveRedisTemplate<String, CommentCachingRedis> reactiveRedisTemplateForComment;
	private ReactiveValueOperations<String, CommentCachingRedis> reactiveRedisTemplateOpsComment;

	@Autowired
	private ReactiveRedisTemplate<String, UserNode> reactiveRedisTemplateForUser;
	private ReactiveValueOperations<String, UserNode> reactiveRedisTemplateOpsUser;

	private ModelMapper modelMapper = new ModelMapper();

	@PostConstruct
	public void setup() {
		reactiveRedisTemplateOpsFoodHut = reactiveRedisTemplateForFoodHut.opsForValue();
		reactiveRedisTemplateOpsComment = reactiveRedisTemplateForComment.opsForValue();
		reactiveRedisTemplateOpsUser = reactiveRedisTemplateForUser.opsForValue();
	}

	@Override
	public void save(FoodHutDto obj) {
		try {
			reactiveRedisTemplateOpsFoodHut.set(obj.getId(), obj).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe();
		} catch (Exception e) {

		}
	}

	@Override
	public void saveComment(String id, CommentCachingRedis obj) {
		try {
			reactiveRedisTemplateOpsComment.set(id, obj).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe();
		} catch (Exception e) {

		}
	}

	@Override
	public Mono<Void> addNewUser(UserNode user) {
		try {
			reactiveRedisTemplateOpsUser.set(user.getPublicId(), user).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe();
			return Mono.empty();
		} catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<Void> updateUser(String key, UserNode user) {
		try {
			getUser(key).publishOn(Schedulers.boundedElastic())
			.subscribeOn(Schedulers.boundedElastic()).map(usr -> {
				usr.setFirstName(user.getFirstName());
				usr.setLastName(user.getLastName());
				usr.setEmail(user.getEmail());
				usr.setAddress(user.getAddress());
				usr.setPic(user.getPic());
				return usr;
			}).doOnNext(i -> {
				reactiveRedisTemplateOpsUser.set(i.getPublicId(), i);
			}).subscribe();
			return Mono.empty();
		}catch (Exception e) {
			return Mono.empty();
		}
		
	}

	@Override
	public Mono<FoodHutDto> getOneFoodHut(String key) {
		try {
			return reactiveRedisTemplateOpsFoodHut.get(key)
					.switchIfEmpty(Mono.empty())
					.publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		}catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Flux<CommentsDto> getAllComments(String key) {
		try {
			return reactiveRedisTemplateOpsComment.get("Comment" + key).map(i -> {
				if (i.getComment() != null) {
					return Flux.fromIterable(i.getComment());
				} else {
					return Flux.fromIterable(new ArrayList<>());
				}
			}).flatMapMany(i -> i).switchIfEmpty(Flux.fromIterable(new ArrayList<>()))
					.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.mapNotNull(i -> modelMapper.map(i, CommentsDto.class));

		} catch (Exception e) {
			return Flux.fromIterable(new ArrayList<>());
		}
	}

	@Override
	public Mono<Void> commentUpdated(String foodId, String commentId, CommentsDto commentDto) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + foodId).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe(i -> {
						List<CommentsDto> comment = new ArrayList<>();
						comment.addAll(i.getComment());
						comment.forEach(commentData -> {
							if (commentData.getId().equals(commentDto.getId())) {
								commentData.setComment(commentDto.getComment());
							}
						});
						i.setComment(comment);
						try {
							reactiveRedisTemplateOpsComment.set("Comment" + foodId, i).subscribe(__ -> {

							});
						} catch (Exception e) {
							// TODO: handle exception
						}
					});
			return Mono.empty();
		} catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<Void> deleteComment(String foodId, String commentId) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + foodId).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe(i -> {
						List<CommentsDto> comment = new ArrayList<>();
						comment.addAll(i.getComment());
						comment.removeIf(j -> j.getId().equals(commentId));
						i.setComment(comment);
						try {
							reactiveRedisTemplateOpsComment.set("Comment" + foodId, i)
									.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
									.subscribe(d -> {

									});
						} catch (Exception e) {
							
						}
					});
			return Mono.empty();
		} catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<Void> addNewComment(String key, CommentsDto comment) {
		try {
			reactiveRedisTemplateOpsComment.get("Comment" + key).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe(i -> {
						List<CommentsDto> commentList = new ArrayList<>();
						commentList.addAll(i.getComment());
						commentList.add(comment);
						i.setComment(commentList);
						reactiveRedisTemplateOpsComment.set("Comment" + key, i).subscribe(__ -> {

						});
					});
		} catch (Exception e) {
		}
		return Mono.empty();
	}

	@Override
	public Mono<UserNode> getUser(String key) {
		try {
			return reactiveRedisTemplateOpsUser.get(key).switchIfEmpty(Mono.empty())
					.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
		} catch (Exception e) {
			return Mono.empty();
		}
	}
}