package com.manura.foodapp.FoodService.repo;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.manura.foodapp.FoodService.entity.UserEntity;

import reactor.core.publisher.Mono;

public interface UserRepo extends ReactiveMongoRepository<UserEntity,Long> {
    Mono<UserEntity> findByPublicId(String publicId);
    Mono<UserEntity> findByEmail(String publicId);
}
