/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.CartService.Table.CartTable;
import com.manura.foodapp.CartService.Table.FoodTable;

import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface CartRepo extends R2dbcRepository<CartTable, Integer> {
   Mono<CartTable> findByFood(FoodTable food);
}
