package com.manura.foodapp.FoodService.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.CommentsEntity;

@Repository
public interface CommentRepo extends ReactiveCrudRepository<CommentsEntity,String> {
}
