package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.UserTable;

import reactor.core.publisher.Mono;

@Repository
public interface UserRepo extends ReactiveCassandraRepository<UserTable,Long> {
	 Mono<UserTable> findByPublicId(String publicId);
	 Mono<UserTable> findByEmail(String emial);
}
