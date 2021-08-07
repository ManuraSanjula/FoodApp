package com.manura.foodapp.FoodService.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodService.Error.Model.FoodNotFoundError;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;
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

	private ModelMapper modelMapper = new ModelMapper();

	@Autowired
	private Utils util;

	@Override
	public Flux<FoodDto> findAll() {
		return Flux.fromIterable(foodRepo.findAll()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Flux<FoodDto> findByType(String type) {
		return Flux.fromIterable(foodRepo.findByType(type)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Mono<FoodDto> findById(String id) {
		Optional<FoodEntity> food = foodRepo.findById(id);
		if (food.isPresent()) {
			return Mono.just(foodRepo.findById(id).isPresent() ? foodRepo.findById(id).get() : null)
					.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.switchIfEmpty(Mono.empty()).map(i -> modelMapper.map(i, FoodDto.class));
		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}

	}

	@Override
	public Mono<FoodDto> save(Mono<FoodDto> foodDto, List<String> foodHutIds) {
		List<FoodHutEntity> foodHutEntities = new ArrayList<FoodHutEntity>();
		List<FoodEntity> foodsEntities = new ArrayList<FoodEntity>();
		return foodDto.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.map(i -> modelMapper.map(i, FoodEntity.class)).map(i -> {
					i.setPublicId(util.generateId(20));
					i.setUnlikes(0);
					i.setLikes(0);
					i.setRating(3);
					i.setCoverImage("FoodCoverImage");
					i.setOffered(true);
					i.setImages(new ArrayList<String>());
					foodHutIds.forEach(id -> {
						FoodHutEntity foodHutEntity = foodHutRepo.findById(id).get();
						if (foodHutEntity != null) {
							foodHutRepo.save(foodHutEntity);
						}
					});
					if (foodHutEntities.isEmpty())
						i.setFoodHuts(null);
					i.setFoodHuts(foodHutEntities);
					i.setComments(new ArrayList<>());
					return i;
				}).flatMap(food -> Mono.just(foodRepo.save(food))).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					foodsEntities.add(i);
					i.getFoodHuts().forEach(foodHut -> {
						foodHut.setFoods(foodsEntities);
						foodHutRepo.save(foodHut);
					});
					Runnable event = () -> {
						pub.pubFood(Mono.just(i), "created");
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
										if (!foodHut.getId().equals(foodHutId)) {
											Optional<FoodHutEntity> foodHutEntity = foodHutRepo.findById(foodHutId);
											if (foodHutEntity.isPresent())
												foodHutEntities.add(foodHutEntity.get());
										}
									});
								});
								List<FoodHutEntity> foodHutEntitiesArray = new ArrayList<FoodHutEntity>();
								if (!foodHutEntities.isEmpty()) {
									foodHutEntities.forEach(u -> {
										foodHutEntitiesArray.add(u);
									});
								}
								if (!foodHutEntitiesArray.isEmpty()) {
									i.setFoodHuts(foodHutEntitiesArray);
								}
							}
						});
						Runnable event = () -> {
							pub.pubFood(Mono.just(i), "update");
						};
						new Thread(event).start();
						return i;
					}).flatMap(fo -> Mono.just(foodRepo.save(fo))).map(i -> {
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
									CommentsEntity commEntity = new CommentsEntity();
									commEntity.setUserImage(usr.getPic());
									commEntity.setCreatedAt(new Date());
									commEntity.setDescription(comm.getDescription());
									commEntity.setFood(food);
									commEntity.setUser(usr);
									return commEntity;
								});
							});
						}).flatMap(i -> i).flatMap(i -> i).publishOn(Schedulers.boundedElastic())
						.subscribeOn(Schedulers.boundedElastic()).flatMap(comm -> Mono.just(commentRepo.save(comm)))
						.map(commEntity -> {
							List<CommentsEntity> commentsEntities = new ArrayList<CommentsEntity>(
									commEntity.getFood().getComments());
							commentsEntities.add(commEntity);
							commEntity.getFood().setComments(commentsEntities);
							foodRepo.save(commEntity.getFood());
							return commEntity;
						}).map(i -> modelMapper.map(i, CommentsDto.class));
			}
		} else {
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}

	@Override
	public Mono<FoodHutDto> saveFoodHut(Mono<FoodHutDto> foodHut) {
		return foodHut.map(i -> modelMapper.map(i, FoodHutEntity.class)).map(i -> {
			return i;
		}).flatMap(i -> Mono.just(foodHutRepo.save(i))).map(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodHutDto> updateFoodHut(Mono<FoodHutDto> foodHut, String id) {
		Optional<FoodHutEntity> findById = this.foodHutRepo.findById(id);
		if (findById.isPresent()) {
			return Mono.just(findById.get()).map(i -> {
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
			return Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}

	}

	@Override
	public Flux<FoodDto> findByTypeAndName(String type, String name) {
		return Flux.fromIterable(foodRepo.findByTypeAndName(type, name)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Mono<UserEntity> getUser(String id) {
		UserEntity userEntity = userRepo.findByPublicId(id);
		if (userEntity != null) {
			return Mono.just(userEntity).publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
					.subscribeOn(Schedulers.boundedElastic());
		} else {
			return Mono.empty();
		}
	}

	@Override
	public Mono<UserEntity> saveUser(Mono<UserDto> user) {
		return user.flatMap(u -> {
			u.setRoles(Arrays.asList("ROLE_USER"));
			u.setAuthorities(
					Arrays.asList("READ_AUTHORITY", "WRITE_AUTHORITY", "DELETE_AUTHORITY", "UPDATE_AUTHORITY"));
			return Mono.just(userRepo.save(modelMapper.map(u, UserEntity.class)));
		}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<UserEntity> updateUser(String id, Mono<UserDto> userDto) {
		return Mono.just(userRepo.findByPublicId(id)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(saveUser(userDto)).mapNotNull(i -> {
					return userDto.map(user -> {
						i.setFirstName(user.getFirstName());
						i.setLastName(user.getLastName());
						i.setEmail(user.getEmail());
						i.setAddress(user.getAddress());
						i.setPic(user.getPic());
						return Mono.just(userRepo.save(i));
					}).flatMap(user -> user);
				}).flatMap(i -> i);

	}

	@Override
	public Flux<FoodDto> findByNames(String name) {
		return Flux.fromIterable(foodRepo.findByName(name)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Flux<CommentsDto> findAllComment(String foodId) {
		Optional<FoodEntity> food = foodRepo.findById(foodId);
		if (food.isPresent()) {
			return Flux.fromIterable(food.get().getComments()).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.map(i -> modelMapper.map(i, CommentsDto.class));
		} else {
			return Flux.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
		}
	}
}