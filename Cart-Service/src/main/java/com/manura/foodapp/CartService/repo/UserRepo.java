/**
 * 
 */
package com.manura.foodapp.CartService.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.manura.foodapp.CartService.Table.UserTable;

/**
 * @author manurasanjula
 *
 */
@Repository
public interface UserRepo extends ReactiveCrudRepository<UserTable, Long>{

}
