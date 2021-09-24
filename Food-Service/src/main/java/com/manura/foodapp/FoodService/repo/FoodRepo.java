package com.manura.foodapp.FoodService.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.FoodEntity;

@Repository
public interface FoodRepo extends MongoRepository<FoodEntity, String> {
	Page<FoodEntity> findByName(String name,Pageable pageable);

	Page<FoodEntity> findByType(String type,Pageable pageable);

    Page<FoodEntity> findByTypeAndName(String type, String name,Pageable pageable);
    
    FoodEntity findByPublicId(String publicId);
    
    Page<FoodEntity> findAll(Pageable pageable);

    
    @Aggregation(pipeline = { "{$group: { _id: $id, total: {$avg: $rating }}}" })
	double avg();
    
    @Query("{$or : [{'name': { $regex: ?0, $options:'i' }}, {'description': { $regex: ?0, $options:'i' }}]}")
    @Tailable
    List<FoodEntity> findFoodByRegexString(final String regexString);
}
