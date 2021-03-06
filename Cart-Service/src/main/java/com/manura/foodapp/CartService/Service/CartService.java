/**
 * 
 */
package com.manura.foodapp.CartService.Service;

import com.manura.foodapp.CartService.Controller.Req.Model.CartReq;
import com.manura.foodapp.CartService.Dto.CartDto;
import com.manura.foodapp.CartService.Table.FoodTable;
import com.manura.foodapp.CartService.Table.UserTable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
public interface CartService {
	Mono<String> saveCart(Mono<CartReq> cart,String email);
	Flux<CartDto> getCart(String id);
	Mono<Void> deleteCart(String user,String id);
	Mono<UserTable> saveUser(Mono<UserTable> user);
	Mono<Boolean> checkOutAll(String email);
	Mono<Boolean> checkOutOne(String email,String publicId);
	Mono<UserTable> getUser(String id);
	Mono<UserTable> updateUser(String id,Mono<UserTable> user);
	Mono<FoodTable> saveFood(Mono<FoodTable> food);
	Mono<FoodTable> updateFood(String id,Mono<FoodTable> food);
}