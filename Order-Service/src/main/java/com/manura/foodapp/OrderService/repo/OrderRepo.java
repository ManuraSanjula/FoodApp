package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import com.manura.foodapp.OrderService.Table.OrderTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepo extends ReactiveCassandraRepository<OrderTable, Long> {
	   Flux<OrderTable> findByUserName(String user);
       Mono<OrderTable> findByPublicId(String publicId);
       Mono<OrderTable> findByUserNameAndFood(String user,String food);
}
