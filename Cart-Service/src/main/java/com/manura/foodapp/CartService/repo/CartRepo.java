/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.CartService.Table.CartTable;

import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface CartRepo extends R2dbcRepository<CartTable, Integer> {
   Mono<CartTable> findByFood(Integer food);
   Mono<CartTable> findByPublicId(String id);
}
