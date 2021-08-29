package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.FoodTable;

import reactor.core.publisher.Mono;

@Repository
public interface FoodRepo extends ReactiveCassandraRepository<FoodTable, Integer> {
	   Mono<FoodTable> findByPublicId(String publicId);

}
