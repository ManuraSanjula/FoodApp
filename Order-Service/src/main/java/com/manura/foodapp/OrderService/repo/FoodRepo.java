package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.FoodTable;

import reactor.core.publisher.Mono;

@Repository
public interface FoodRepo extends ReactiveCassandraRepository<FoodTable, Long> {
	@AllowFiltering
	Mono<FoodTable> findByPublicId(String publicId);
}
