package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.BillingAndDeliveryAddressTable;

import reactor.core.publisher.Flux;

@Repository
public interface BillingAndDeliveryAddressRepo extends ReactiveCassandraRepository<BillingAndDeliveryAddressTable, Long>{
   Flux<BillingAndDeliveryAddressTable> findByUserId(String userId);
}
