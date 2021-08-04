package com.manura.foodapp.FoodService.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.CommentsEntity;

@Repository
public interface CommentRepo extends ReactiveMongoRepository<CommentsEntity,String> {
}
