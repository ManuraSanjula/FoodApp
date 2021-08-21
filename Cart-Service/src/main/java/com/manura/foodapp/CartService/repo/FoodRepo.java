/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.CartService.Table.FoodTable;

import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface FoodRepo extends ReactiveCrudRepository<FoodTable,Integer> {
   Mono<FoodTable> findByPublicId(String publicId);
}
