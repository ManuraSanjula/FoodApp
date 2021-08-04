package com.manura.foodapp.FoodService.repo;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.FoodEntity;

import reactor.core.publisher.Flux;

@Repository
public interface FoodRepo extends ReactiveMongoRepository<FoodEntity, String> {
    Flux<FoodEntity> findByName(String name);

    Flux<FoodEntity> findByType(String type);

    Flux<FoodEntity> findByTypeAndName(String type, String name);
}
