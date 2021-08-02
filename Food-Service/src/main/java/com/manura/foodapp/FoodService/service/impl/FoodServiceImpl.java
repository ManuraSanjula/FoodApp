package com.manura.foodapp.FoodService.service.impl;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodService.repo.FoodHutRepo;
import com.manura.foodapp.FoodService.repo.FoodRepo;
import com.manura.foodapp.FoodService.repo.UserRepo;
import com.manura.foodapp.FoodService.service.FoodService;
import com.manura.foodapp.FoodService.util.ErrorMessages;
import com.manura.foodapp.FoodService.util.Utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.manura.foodapp.FoodService.Error.Model.FoodNotFoundError;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.dto.FoodHutDto;
import com.manura.foodapp.FoodService.entity.CommentsEntity;
import com.manura.foodapp.FoodService.entity.FoodEntity;
import com.manura.foodapp.FoodService.entity.FoodHutEntity;
import com.manura.foodapp.FoodService.entity.UserEntity;
import com.manura.foodapp.FoodService.messaging.Pub;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    FoodRepo foodRepo;

    @Autowired
    FoodHutRepo foodHutRepo;

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

        List<String> foodIDs = new ArrayList<String>();

        return foodDto.publishOn(Schedulers.boundedElastic())
             .subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, FoodEntity.class)).map(i -> {

                    i.setPublicId(util.generateId(20));
                 
                    foodHutIds.forEach(id -> {

                        FoodHutEntity foodHutEntity = foodHutRepo.findById(id).publishOn(Schedulers.boundedElastic())
                                .subscribeOn(Schedulers.boundedElastic()).block();
                        if(foodHutEntity !=null) {
                        	 foodIDs.addAll(foodHutEntity.getFoodIds());

                             if (foodIDs.isEmpty())
                                 foodIDs.add(i.getPublicId());

                             if (!foodIDs.contains(i.getPublicId())) {
                                 foodIDs.add(i.getPublicId());
                             }

                             foodHutEntity.setFoodIds(foodIDs);
                             foodHutRepo.save(foodHutEntity);

                             if (foodHutEntity != null)
                                 foodHutEntities.add(foodHutEntity);
                        }
                    });

                    if (foodHutEntities.isEmpty())
                        i.setFoodHuts(null);
                    i.setFoodHuts(foodHutEntities);
                    return i;

                }).flatMap(foodRepo::save).map(i -> {
//                    pub.pubFood(Mono.just(i), "created");
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
                    pub.pubFood(Mono.just(i), "update");

                    return i;
                }).flatMap(foodRepo::save).map(i -> {
                    return modelMapper.map(i, FoodDto.class);
                });

    }

    @Override
    public Mono<CommentsDto> saveComment(String id, Mono<CommentsDto> comment) {

        return comment.map(userComment -> {
            Mono<UserEntity> foundUser = userRepo.findByPublicId(userComment.getUser());
            foundUser.subscribe(u -> {
                if (u == null)
                    System.out.println("Dup");
                userComment.setUserImage(u.getPic());
                userComment.setCreatedAt(new Date());
            });
            return userComment;

        }).map(i -> modelMapper.map(i, CommentsEntity.class)).map(i -> {
            return i;
        }).map(userComment -> {

            AtomicBoolean set = new AtomicBoolean();

            foodRepo.findById(id).switchIfEmpty(Mono.error(new FoodNotFoundError("Food Not Found"))).map(food -> {

                if (food.getComments() == null)
                    food.setComments(Arrays.asList(userComment));

                List<CommentsEntity> comments = new ArrayList<CommentsEntity>();
                comments.addAll(food.getComments());

                comments.add(userComment);
                food.setComments(comments);

                return food;

            }).flatMap(foodRepo::save).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
                    .subscribe(com -> {
                        if (com.getComments().isEmpty()) {
                            set.set(false);
                        }
                        if (com.getComments() == null) {
                            set.set(false);
                        }

                        set.set(true);
                        pub.pubFood(Mono.just(com), "update");
                    });

            if (!set.get())
                return null;
            else {

                return userComment;
            }

        }).map(i -> modelMapper.map(i, CommentsDto.class)).publishOn(Schedulers.boundedElastic())
                .subscribeOn(Schedulers.boundedElastic());
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
        return this.foodHutRepo.findById(id)
                .flatMap(p -> foodHut.map(i -> modelMapper.map(i, FoodHutEntity.class)).doOnNext(e -> e.setId(id)))
                .map(i -> {

                    i.getFoodIds().forEach(foodId -> {
                        Mono<FoodEntity> foodEntityMono = foodRepo.findById(foodId)
                                .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
                                .map(foods -> {

                                    List<FoodHutEntity> foodHuts = new ArrayList<>();
                                    foodHuts.addAll(foods.getFoodHuts());

                                    for (int count = 0; count < foodHuts.size(); count++) {

                                        String foodId1 = foodHuts.get(count).getId();
                                        String foodId2 = i.getId();

                                        if (foodId2 != foodId1) {
                                            foodHuts.remove(count);
                                        }
                                    }
                                    return foods;

                                }).flatMap(foodRepo::save);

                        foodEntityMono.subscribe(k -> {
                            System.out.println(k.getName());
                        });

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
}