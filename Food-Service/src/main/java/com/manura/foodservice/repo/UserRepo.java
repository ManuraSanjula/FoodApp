package com.manura.foodservice.repo;

import com.manura.foodservice.entity.UserEntity;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Mono;

public interface UserRepo extends ReactiveMongoRepository<UserEntity,Long> {
    Mono<UserEntity> findByPublicId(String publicId);
}
