package com.manura.foodapp.FoodService.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.manura.foodapp.FoodService.entity.UserEntity;

public interface UserRepo extends MongoRepository<UserEntity,Long> {
    UserEntity findByPublicId(String publicId);
    UserEntity findByEmail(String publicId);
}
