package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.BillingAndDeliveryAddressTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BillingAndDeliveryAddressRepo
		extends ReactiveCassandraRepository<BillingAndDeliveryAddressTable, Long> {
	@AllowFiltering
	Flux<BillingAndDeliveryAddressTable> findByUserId(String userId);
	
	@AllowFiltering
	Mono<BillingAndDeliveryAddressTable> findById(Long id);
}
