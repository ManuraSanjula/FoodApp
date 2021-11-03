package com.manura.foodapp.OrderService.repo;

import java.sql.Timestamp;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.UserTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepo extends ReactiveCassandraRepository<UserTable, Long> {
	@AllowFiltering
	Mono<UserTable> findByPublicId(String publicId);

	@AllowFiltering
	Mono<UserTable> findByEmail(String emial);
	@AllowFiltering
	Flux<UserTable> findAllByAccountNonLockedAndLastModifiedDateIsBefore(Boolean locked, Timestamp timestamp);
}
