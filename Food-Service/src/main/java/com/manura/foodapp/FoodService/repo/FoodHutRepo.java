package com.manura.foodapp.FoodService.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.FoodHutEntity;

@Repository
public interface FoodHutRepo extends MongoRepository<FoodHutEntity,String> {
}
