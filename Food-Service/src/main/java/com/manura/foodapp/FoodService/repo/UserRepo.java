package com.manura.foodapp.FoodService.repo;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.manura.foodapp.FoodService.entity.UserEntity;

import reactor.core.publisher.Mono;

public interface UserRepo extends ReactiveCrudRepository<UserEntity,Long> {
    Mono<UserEntity> findByPublicId(String publicId);
}
