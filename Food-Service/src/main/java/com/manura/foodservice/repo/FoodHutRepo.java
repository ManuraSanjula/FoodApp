package com.manura.foodservice.repo;

import com.manura.foodservice.entity.FoodHutEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodHutRepo extends ReactiveMongoRepository<FoodHutEntity,String> {
}
