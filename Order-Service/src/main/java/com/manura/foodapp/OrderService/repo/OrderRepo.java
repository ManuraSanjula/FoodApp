package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.OrderTable;

import reactor.core.publisher.Flux;

@Repository
public interface OrderRepo extends ReactiveCassandraRepository<OrderTable, Integer> {
	   Flux<OrderTable> findByUserName(String user);

}
