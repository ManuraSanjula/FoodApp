/**
 * 
 */
package com.manura.foodapp.CartService.Service;

import com.manura.foodapp.CartService.Controller.Req.Model.CartReq;
import com.manura.foodapp.CartService.Table.FoodTable;
import com.manura.foodapp.CartService.Table.UserTable;
import com.manura.foodapp.CartService.Table.Dto.CartDto;

import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
public interface CartService {
	Mono<CartDto> saveCart(Mono<CartReq> cart);

	Mono<Void> deleteCart(String id);

	Mono<UserTable> saveUser(Mono<UserTable> user);

	Mono<UserTable> updateUser(String id,Mono<UserTable> user);
	
	Mono<FoodTable> saveFood(Mono<FoodTable> food);

	Mono<FoodTable> updateFood(String id,Mono<FoodTable> food);
}
