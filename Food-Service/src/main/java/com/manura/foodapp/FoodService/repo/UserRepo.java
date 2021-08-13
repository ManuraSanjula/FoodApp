package com.manura.foodapp.FoodService.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.UserEntity;

@Repository

public interface UserRepo extends MongoRepository<UserEntity,Long> {
    UserEntity findByPublicId(String publicId);
    UserEntity findByEmail(String publicId);
}
