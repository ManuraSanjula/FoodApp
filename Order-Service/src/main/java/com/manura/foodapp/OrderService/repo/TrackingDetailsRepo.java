package com.manura.foodapp.OrderService.repo;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.OrderService.Table.TrackingDetailsTable;

@Repository
public interface TrackingDetailsRepo extends ReactiveCassandraRepository<TrackingDetailsTable, Long>{

}
