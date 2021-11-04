package com.manura.foodapp.FoodService.repo;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.UserEntity;

@Repository

public interface UserRepo extends MongoRepository<UserEntity,String> {
    UserEntity findByEmail(String publicId);
	List<UserEntity> findAllByAccountNonLockedAndLastModifiedDateIsBefore(Boolean locked, Timestamp timestamp);

}
