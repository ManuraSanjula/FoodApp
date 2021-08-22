/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.CartService.Table.UserTable;

import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface UserRepo extends R2dbcRepository<UserTable, Integer>{
   Mono<UserTable> findByPublicId(String publicId);
   Mono<UserTable> findByEmail(String emial);

}

