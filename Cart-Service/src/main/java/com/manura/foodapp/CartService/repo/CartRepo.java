/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.CartService.Table.CartTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface CartRepo extends R2dbcRepository<CartTable, Integer> {
   Mono<CartTable> findByUserAndFood(String user,String food);
   Flux<CartTable> findByUser(String user);
   Mono<CartTable> findByPublicId(String id);
}
