package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.RefundTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface RefundRepo extends ReactiveCassandraRepository<RefundTable,Long> {
   Mono<RefundTable> findByOrderIdAndUserId(String orderId);
   Flux<RefundTable> findByUserId(String userId);
}
