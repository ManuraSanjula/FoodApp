/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface FoodRepo extends ReactiveCrudRepository<FoodRepo,Long> {

}
