package com.manura.foodapp.FoodService.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodService.Error.Model.FoodNotFoundError;
import com.manura.foodapp.FoodService.Redis.Model.CommentCachingRedis;
import com.manura.foodapp.FoodService.controller.Model.Res.HalfFoodRes;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodCommentDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;
import com.manura.foodapp.FoodService.dto.FoodHutDtoForSubSaving;
import com.manura.foodapp.FoodService.dto.UserDto;
import com.manura.foodapp.FoodService.entity.CommentsEntity;
import com.manura.foodapp.FoodService.entity.FoodEntity;
import com.manura.foodapp.FoodService.entity.FoodHutEntity;
import com.manura.foodapp.FoodService.entity.UserEntity;
import com.manura.foodapp.FoodService.messaging.Pub;
import com.manura.foodapp.FoodService.repo.CommentRepo;
import com.manura.foodapp.FoodService.repo.FoodHutRepo;
import com.manura.foodapp.FoodService.repo.FoodRepo;
import com.manura.foodapp.FoodService.repo.UserRepo;
import com.manura.foodapp.FoodService.service.FoodService;
import com.manura.foodapp.FoodService.util.ErrorMessages;
import com.manura.foodapp.FoodService.util.Utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class FoodServiceImpl implements FoodService {

	@Autowired
	private FoodRepo foodRepo;

	@Autowired
	private FoodHutRepo foodHutRepo;

	@Autowired
	private CommentRepo commentRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private Pub pub;

	@Autowired
	private Mono<RSocketRequester> rSocketRequester;

	private ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private Utils util;

	@Autowired
	private RedisServiceImpl redisServiceImpl;

	private Mono<FoodDto> ifFoodAbsentInCache(String id) {
		Optional<FoodEntity> food = foodRepo.findById(id);
		if (food.isPresent()) {
			return Mono.just(food.get()).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.map(i -> modelMapper.map(i, FoodDto.class)).map(i -> {
						redisServiceImpl.save(i);
						return i;
					});
		} else {
			return Mono.empty();
		}
	}

	@Override
	public Flux<HalfFoodRes> findAll(int page, int size) {
		Pageable paging = PageRequest.of(page, size);
		return Flux.fromIterable(foodRepo.findAll(paging).getContent()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class));
	}

	@Override
	public Flux<HalfFoodRes> findByType(String type, int page, int size) {
		Pageable paging = PageRequest.of(page, size);
		return Flux.fromIterable(foodRepo.findByType(type, paging).getContent()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class));
	}

	@Override
	public Mono<FoodDto> findById(String id) {
		return redisServiceImpl.getFood(id).switchIfEmpty(ifFoodAbsentInCache(id))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodDto> save(Mono<FoodDto> foodDto, List<String> foodHutIds) {
		Set<FoodHutEntity> foodHutEntities = new HashSet<FoodHutEntity>();
		return foodDto.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.map(i -> modelMapper.map(i, FoodEntity.class)).map(i -> {
					i.setPublicId(util.generateId(20));
					i.setUnlikes(0);
					i.setLikes(0);
					i.setRating(3.0);
					i.setCoverImage("/food-image/FoodCoverImage");
					i.setOffered(true);
					i.setImages(new ArrayList<String>());
					foodHutIds.forEach(id -> {
						Optional<FoodHutEntity> foodHutEntity = foodHutRepo.findById(id);
						if (foodHutEntity.isPresent()) {
							foodHutEntities.add(foodHutEntity.get());
						}
					});
					i.setFoodHuts(foodHutEntities);
					i.setComments(new ArrayList<>());
					return i;
				}).flatMap(food -> Mono.just(foodRepo.save(food))).doOnNext(i->{
					Runnable updateFoodHut = () -> {
						foodHutIds.forEach(foodId -> {
							Optional<FoodHutEntity> foodHutEntity = foodHutRepo.findById(foodId);
							if (foodHutEntity.isPresent()) {
								foodHutEntity.get().getFoods().add(i);
								foodHutRepo.save(foodHutEntity.get());
							}
						});
					};
					new Thread(updateFoodHut).start();
				}).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					Runnable event = () -> {
						pub.pubFood(Mono.just(i), "created");
						redisServiceImpl.save(modelMapper.map(i, FoodDto.class));
					};
					new Thread(event).start();
					return modelMapper.map(i, FoodDto.class);
				});
	}

	@Override
	public Mono<FoodDto> update(String id, Mono<FoodDto> foodDto, List<String> foodHutIds) {
		Set<FoodHutEntity> foodHutEntities = new HashSet<FoodHutEntity>();
		Optional<FoodEntity> food = foodRepo.findById(id);
		if (food.isPresent()) {
			return Mono.just(food.get())
					.switchIfEmpty(Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
					.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(i -> {
						foodDto.subscribe(updateReq -> {
							if (updateReq.getName() != null) {
								i.setName(updateReq.getName());
							}
							if (updateReq.getDescription() != null) {
								i.setDescription(updateReq.getDescription());
							}
							if (updateReq.getType() != null) {
								i.setType(updateReq.getType());
							}
							if (updateReq.getPrice() != null) {
								i.setPrice(updateReq.getPrice());
							}
							if (updateReq.getNutrition() != null) {
								i.setNutrition(updateReq.getNutrition());
							}
							if (foodHutIds != null) {
								foodHutIds.forEach(foodHutId -> {
									
									foodHutEntities.addAll(i.getFoodHuts());
									i.getFoodHuts().forEach(foodHut -> {
										Optional<FoodHutEntity> foodHutEntity = foodHutRepo.findById(foodHutId);
										if (foodHutEntity.isPresent()) {
											foodHutEntities.add(foodHutEntity.get());
											
										}	
									});
								});
								i.setFoodHuts(foodHutEntities);
							}
						});
						Runnable event = () -> {
							pub.pubFood(Mono.just(i), "update");
							redisServiceImpl.save(modelMapper.map(i, FoodDto.class));
						};
						new Thread(event).start();
						return i;
					}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(i -> {
						redisServiceImpl.updateCommentIFFoodUpdated(id, modelMapper.map(i, FoodCommentDto.class));
						return i;
					}).flatMap(fo -> Mono.just(foodRepo.save(fo)))
					.doOnNext(i->{
						Runnable updateFoodHut = () -> {
							foodHutIds.forEach(foodId -> {
								Optional<FoodHutEntity> foodHutEntity = foodHutRepo.findById(foodId);
								if (foodHutEntity.isPresent()) {
									foodHutEntity.get().getFoods().add(i);
									foodHutRepo.save(foodHutEntity.get());
								}
							});
						};
						new Thread(updateFoodHut).start();
					})
					.map(i -> {
						return modelMapper.map(i, FoodDto.class);
					});

		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}

	}

	@Override
	public Mono<CommentsDto> saveComment(String id, Mono<CommentsDto> comment, String user) {
		Optional<FoodEntity> foundfood = foodRepo.findById(id);
		if (foundfood.isPresent()) {
			UserEntity userEntity = userRepo.findByEmail(user);
			if (userEntity == null) {
				return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
			} else {
				return Mono.just(userEntity).publishOn(Schedulers.boundedElastic())
						.switchIfEmpty(
								Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
						.subscribeOn(Schedulers.boundedElastic()).map(usr -> {
							return Mono.just(foundfood.get()).map(food -> {
								return comment.map(comm -> {
									food.setRating((food.getRating() + comm.getRating()));
									foodRepo.save(food);
									Double avg = ((avg()) / 2);
									if (avg < 2) {
										avg = 3.0;
									}
									if (avg >= 5) {
										avg = 5.0;
									}
									food.setRating(avg);
									foodRepo.save(food);
									CommentsEntity commEntity = new CommentsEntity();
									commEntity.setUserImage(usr.getPic());
									commEntity.setCreatedAt(new Date());
									commEntity.setDescription(comm.getDescription());
									commEntity.setFood(food);
									commEntity.setUser(usr);
									commEntity.setRating(comm.getRating());
									return commEntity;
								});
							});
						}).flatMap(i -> i).flatMap(i -> i).publishOn(Schedulers.boundedElastic())
						.subscribeOn(Schedulers.boundedElastic()).flatMap(comm -> Mono.just(commentRepo.save(comm)))
						.map(commEntity -> {
							List<CommentsEntity> commentsEntities = new ArrayList<CommentsEntity>();
							commentsEntities.addAll(commEntity.getFood().getComments());
							commentsEntities.add(commEntity);
							commEntity.getFood().setComments(commentsEntities);
							foodRepo.save(commEntity.getFood());
							return commEntity;
						}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
						.map(i -> modelMapper.map(i, CommentsDto.class)).map(i -> {
							redisServiceImpl.addNewComment("Comment" + id, i);
							return i;
						});
			}
		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}

	@Override
	public Mono<FoodHutDto> saveFoodHut(Mono<FoodHutDtoForSubSaving> foodHut) {
		return foodHut.map(i -> modelMapper.map(i, FoodHutEntity.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					return i;
				}).flatMap(i -> Mono.just(foodHutRepo.save(i))).map(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodHutDto> updateFoodHut(Mono<FoodHutDtoForSubSaving> foodHut, String id) {
		Optional<FoodHutEntity> findById = this.foodHutRepo.findById(id);
		if (findById.isPresent()) {
			return Mono.just(findById.get()).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> {
						foodHut.map(dto -> {
							i.setName(dto.getName());
							i.setPhoneNumbers(dto.getPhoneNumbers());
							i.setAddress(dto.getAddress());
							i.setImage(dto.getImage());
							return i;
						});
						return i;
					}).flatMap(i -> Mono.just(foodHutRepo.save(i))).map(i -> modelMapper.map(i, FoodHutDto.class))
					.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
		} else {
			return saveFoodHut(foodHut).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
		}

	}

	@Override
	public Flux<HalfFoodRes> findByTypeAndName(String type, String name, int page, int size) {
		Pageable paging = PageRequest.of(page, size);
		return Flux.fromIterable(foodRepo.findByTypeAndName(type, name, paging).getContent()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class));
	}

	private Mono<UserEntity> ifUserAbsentInCache(String id) {
		Optional<UserEntity> userEntity = userRepo.findById(id);
		if (userEntity.isPresent()) {
			return Mono.just(userEntity.get()).publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
					.subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
						redisServiceImpl.addNewUser(i).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).subscribe();
					});
		} else {
			return Mono.empty();
		}
	}

	@Override
	public Mono<UserEntity> getUser(String id) {
		return redisServiceImpl.getUser(id).publishOn(Schedulers.boundedElastic())
				.switchIfEmpty(ifUserAbsentInCache(id)).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<UserEntity> saveUser(Mono<UserDto> user) {
		return user.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).flatMap(u -> {
			return Mono.just(userRepo.save(modelMapper.map(u, UserEntity.class)));
		}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
			redisServiceImpl.addNewUser(i).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe();
		});
	}

	@Override
	public Mono<UserEntity> updateUser(String id, Mono<UserDto> userDto) {
		return userDto.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).flatMap(user -> {
			Optional<UserEntity> userEntityOpt = userRepo.findById(id);
			if (userEntityOpt.isPresent()) {
				UserEntity userEntity = userEntityOpt.get();
				userEntity.setFirstName(user.getFirstName());
				userEntity.setLastName(user.getLastName());
				userEntity.setEmail(user.getEmail());
				userEntity.setAddress(user.getAddress());
				userEntity.setPic(user.getPic());
				return Mono.just(userRepo.save(userEntity)).publishOn(Schedulers.boundedElastic())
						.subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
							redisServiceImpl.updateUser(id, i).subscribe();
						});
			} else {
				return saveUser(Mono.just(user)).doOnNext(i -> {
					redisServiceImpl.addNewUser(i).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).subscribe();
				});
			}
		});
	}

	@Override
	public Flux<HalfFoodRes> findByNames(String name, int page, int size) {
		Pageable paging = PageRequest.of(page, size);
		return Flux.fromIterable(foodRepo.findByName(name, paging).getContent()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class));
	}

	private Flux<CommentsDto> ifCommentAbsentInCache(String id) {
		Optional<FoodEntity> food = foodRepo.findById(id);
		if (food.isPresent()) {
			return Flux.fromIterable(food.get().getComments()).publishOn(Schedulers.boundedElastic())
					.switchIfEmpty(Flux.fromIterable(new ArrayList<>())).map(i -> modelMapper.map(i, CommentsDto.class))
					.subscribeOn(Schedulers.boundedElastic()).doOnNext((i) -> {
						List<CommentsDto> commentsDtos = new ArrayList<>();
						commentsDtos.add(i);
						CommentCachingRedis commentCachingRedis = new CommentCachingRedis();
						commentCachingRedis.setName("Comment" + id);
						commentCachingRedis.setComment(commentsDtos);
						redisServiceImpl.saveComment(commentCachingRedis);
					});
		} else {
			return Flux.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}

	@Override
	public Flux<CommentsDto> findAllComment(String foodId) {
		return redisServiceImpl.findAllComment(foodId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(ifCommentAbsentInCache(foodId));
	}

	@Override
	public Mono<FoodDto> uploadCoverImage(String id, Mono<FilePart> filePartFlux) {
		Optional<FoodEntity> foodEntity = foodRepo.findById(id);
		if (foodEntity.isPresent()) {
			return Mono.just(foodEntity.get()).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(food -> {

						String name = filePartFlux.publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).map(part -> {
									return this.rSocketRequester.publishOn(Schedulers.boundedElastic())
											.subscribeOn(Schedulers.boundedElastic())
											.map(rsocket -> rsocket.route("file.upload.food").data(part.content()))
											.map(r -> r.retrieveFlux(String.class)).flatMapMany(s -> s);
								}).flatMapMany(s -> s).blockLast();

						String image = ("/food-image/" + name);
						food.setCoverImage(image);
						return foodRepo.save(food);

					}).map(i -> {
						Runnable event = () -> {
							pub.pubFood(Mono.just(i), "update");
							redisServiceImpl.save(modelMapper.map(i, FoodDto.class));
						};
						new Thread(event).start();
						return i;
					}).map(i -> modelMapper.map(i, FoodDto.class)).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}

	@Override
	public Mono<FoodDto> uploadImages(String id, Flux<FilePart> filePartFlux) {
		Optional<FoodEntity> foodEntity = foodRepo.findById(id);
		if (foodEntity.isPresent()) {
			return Mono.just(foodEntity.get()).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(food -> {
						List<String> urls = new ArrayList<String>();
						return filePartFlux.publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).map(file -> {

									String image = this.rSocketRequester
											.map(rsocket -> rsocket.route("file.upload.food").data(file.content()))
											.mapNotNull(r -> r.retrieveFlux(String.class)).flatMapMany(s -> s)
											.distinct().publishOn(Schedulers.boundedElastic()).blockFirst();

									if (image != null) {
										String fullImageUril = ("/food-image/" + image);
										urls.add(fullImageUril);
									}
									return urls;
								}).map(url -> {
									foodEntity.get().setImages(url);
									return foodEntity;
								}).blockLast().map(g -> {
									return foodRepo.save(g);
								}).get();
					}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(i -> {
						Runnable event = () -> {
							pub.pubFood(Mono.just(i), "update");
							redisServiceImpl.save(modelMapper.map(i, FoodDto.class));
						};
						new Thread(event).start();
						return i;
					}).map(i -> foodRepo.save(i)).map(i -> modelMapper.map(i, FoodDto.class));
		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}

	@Override
	public Flux<HalfFoodRes> findByLocationNear(Point p, Distance d) {
		return Flux.fromIterable(this.foodHutRepo.findByLocationNear(p, d)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).mapNotNull(i -> {
				     Set<FoodEntity> foods = new HashSet<>();
				     foods.addAll(i.getFoods());
				     return Flux.fromIterable(foods);
				}).flatMap(i -> i).map(i -> modelMapper.map(i, HalfFoodRes.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Flux.fromIterable(new ArrayList<>())).distinct();
	}

	@Override
	public Double avg() {
		return foodRepo.avg();
	}

	@Override
	public Mono<CommentsDto> updateComment(String id, String foodId, String desc) {
		Optional<CommentsEntity> comment = commentRepo.findById(id);
		Optional<FoodEntity> food = foodRepo.findById(foodId);
		if (comment.isPresent() && food.isPresent()) {
			return Mono.just(comment.get()).doOnNext(i -> i.setDescription(desc)).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).flatMap(i -> Mono.just(commentRepo.save(i)))
					.doOnNext(i -> {
						redisServiceImpl
								.commentUpdated(i.getFood().getId(), i.getId(), modelMapper.map(i, CommentsDto.class))
								.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
					}).map(i -> modelMapper.map(i, CommentsDto.class));
		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}

	@Override
	public Mono<Void> deleteComment(String foodId, String commentID) {
		Optional<FoodEntity> food = foodRepo.findById(foodId);
		if (food.isPresent()) {
			return Mono.just(food.get()).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.flatMap(i -> {
						Optional<CommentsEntity> comment = commentRepo.findById(commentID);
						if (comment.isPresent()) {
							List<CommentsEntity> comments = Collections.synchronizedList(new ArrayList<>());
							comments.addAll(i.getComments());
							comments.removeIf(j -> j.getId().equals(comment.get().getId()));
							commentRepo.deleteById(commentID);
							redisServiceImpl.deleteComment(foodId, commentID);
							i.setComments(comments);
							foodRepo.save(i);
							return Mono.empty();

						} else {
							return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
						}
					});
		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}

	@Override
	public Flux<HalfFoodRes> search(String regex) {
		String fullRegex = ("/^" + regex + "/");
		return Flux.fromIterable(foodRepo.findFoodByRegexString(fullRegex))
				.map(i -> modelMapper.map(i, HalfFoodRes.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}
}




