package com.manura.foodapp.FoodService.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.FoodHutEntity;

@Repository
public interface FoodHutRepo extends ReactiveCrudRepository<FoodHutEntity,String> {
}
