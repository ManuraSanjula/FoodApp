package com.manura.foodapp.FoodService.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
	FoodRepo foodRepo;

	@Autowired
	FoodHutRepo foodHutRepo;

	@Autowired
	CommentRepo commentRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	Pub pub;

	ModelMapper modelMapper = new ModelMapper();

	@Autowired
	Utils util;

	@Override
	public Flux<FoodDto> findAll() {
		return foodRepo.findAll().publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Flux<FoodDto> findByName(String name) {
		return foodRepo.findByName(name).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Flux<FoodDto> findByType(String type) {
		return foodRepo.findByType(type).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Mono<FoodDto> findById(String id) {
		List<FoodHutDto> foodHutDto = new ArrayList<FoodHutDto>();
		List<CommentsDto> commentDto = new ArrayList<CommentsDto>();
		return foodRepo.findById(id).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.mapNotNull(i -> {
					FoodDto foodDto = modelMapper.map(i, FoodDto.class);
					if (foodDto.getFoodHuts() != null) {
						foodDto.getFoodHuts().forEach(foodHut -> {
							foodHutDto.add(modelMapper.map(foodHut, FoodHutDto.class));
						});
					}
					if (foodDto.getComments() != null) {
						foodDto.getComments().forEach(foodHut -> {
							commentDto.add(modelMapper.map(foodHut, CommentsDto.class));
						});
					}
					foodDto.setFoodHuts(foodHutDto);
					foodDto.setComments(commentDto);
					return foodDto;
				});
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
						FoodHutEntity foodHutEntity = foodHutRepo.findById(id).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).block();
						if (foodHutEntity != null) {
							foodHutRepo.save(foodHutEntity);
						}
					});
					if (foodHutEntities.isEmpty())
						i.setFoodHuts(null);
					i.setFoodHuts(foodHutEntities);
					i.setComments(new ArrayList<>());
					return i;
				}).flatMap(foodRepo::save).publishOn(Schedulers.boundedElastic())
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
		return foodRepo.findById(id)
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
										FoodHutEntity foodHutEntity = foodHutRepo.findById(foodHutId)
												.publishOn(Schedulers.boundedElastic())
												.subscribeOn(Schedulers.boundedElastic()).block();
										if (foodHutEntity != null)
											foodHutEntities.add(foodHutEntity);
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
				}).flatMap(foodRepo::save).map(i -> {
					return modelMapper.map(i, FoodDto.class);
				});

	}

	@Override
	public Mono<CommentsDto> saveComment(String id, Mono<CommentsDto> comment, String user) {
		return comment.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(comm -> {
			return userRepo.findByPublicId(user).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic())
					.switchIfEmpty(Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
					.mapNotNull(u -> {
						return foodRepo.findById(id).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic())
								.switchIfEmpty(Mono
										.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
								.map(food -> {
									CommentsEntity commEntity = new CommentsEntity();
									commEntity.setUserImage(u.getPic());
									commEntity.setCreatedAt(new Date());
									commEntity.setDescription(comm.getDescription());
									List<CommentsEntity> comments = new ArrayList<>();
									if (!food.getComments().isEmpty() || food.getComments() != null) {
										comments.addAll(food.getComments());
									}
									commEntity.setFood(food);
									comments.add(commEntity);
									food.setComments(comments);
									foodRepo.save(food);
									commEntity.setUser(u);
									return commEntity;
								});
					});
		}).flatMap(i -> i).flatMap(i -> i).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(commentRepo::save)
				.map(i -> modelMapper.map(i, CommentsDto.class));
	}

	@Override
	public Mono<FoodHutDto> saveFoodHut(Mono<FoodHutDto> foodHut) {
		return foodHut.map(i -> modelMapper.map(i, FoodHutEntity.class)).map(i -> {
			return i;
		}).flatMap(foodHutRepo::save).map(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodHutDto> updateFoodHut(Mono<FoodHutDto> foodHut, String id) {
		return this.foodHutRepo.findById(id).map(i -> {
			foodHut.map(dto -> {
				i.setName(dto.getName());
				i.setPhoneNumbers(dto.getPhoneNumbers());
				i.setAddress(dto.getAddress());
				i.setImage(dto.getImage());
				return i;
			});
			return i;
		}).flatMap(foodHutRepo::save).map(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Flux<FoodDto> findByTypeAndName(String type, String name) {
		return foodRepo.findByTypeAndName(type, name).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Mono<UserEntity> getUser(String id) {
		return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
				.subscribeOn(Schedulers.boundedElastic());

	}

	@Override
	public Mono<UserEntity> saveUser(Mono<UserDto> user) {
		return user.flatMap(u -> {
			u.setRoles(Arrays.asList("ROLE_USER"));
			u.setAuthorities(
					Arrays.asList("READ_AUTHORITY", "WRITE_AUTHORITY", "DELETE_AUTHORITY", "UPDATE_AUTHORITY"));
			return userRepo.save(modelMapper.map(u, UserEntity.class));
		}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<UserEntity> updateUser(String id, Mono<UserDto> userDto) {
		return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(saveUser(userDto)).mapNotNull(i -> {
					return userDto.map(user->{
						i.setFirstName(user.getFirstName());
						i.setLastName(user.getLastName());
						i.setEmail(user.getEmail());
						i.setAddress(user.getAddress());
						i.setPic(user.getPic());
						return userRepo.save(i);
					}).flatMap(user->user);
				}).flatMap(i->i);

		
	}
}
