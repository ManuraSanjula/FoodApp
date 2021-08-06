package com.manura.foodapp.FoodService.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.FoodEntity;

@Repository
public interface FoodRepo extends MongoRepository<FoodEntity, String> {
    List<FoodEntity> findByName(String name);

    List<FoodEntity> findByType(String type);

    List<FoodEntity> findByTypeAndName(String type, String name);
    
    FoodEntity findByPublicId(String publicId);
}
