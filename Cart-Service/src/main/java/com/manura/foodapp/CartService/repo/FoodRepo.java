/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.CartService.Table.FoodTable;

import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface FoodRepo extends R2dbcRepository<FoodTable,Integer> {
   Mono<FoodTable> findByPublicId(String publicId);
}
