package com.manura.foodapp.FoodService.repo;

import java.util.List;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.FoodService.entity.FoodHutEntity;

@Repository
public interface FoodHutRepo extends MongoRepository<FoodHutEntity,String> {
	  List<FoodHutEntity> findByLocationNear(Point p, Distance d);

}
