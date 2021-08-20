/**
 * 
 */
package com.manura.foodapp.CartService.Service;

import com.manura.foodapp.CartService.Table.Dto.CartDto;
import com.manura.foodapp.CartService.Table.Dto.FoodDto;
import com.manura.foodapp.CartService.Table.Dto.UserDto;

import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
public interface CartService {
	Mono<CartDto> saveCart(Mono<CartDto> cart);

	Mono<Void> deleteCart(String id);

	Mono<UserDto> saveUser(Mono<UserDto> user);

	Mono<UserDto> updateUser(Mono<UserDto> user);
	
	Mono<FoodDto> saveFood(Mono<FoodDto> food);

	Mono<FoodDto> updateFood(Mono<FoodDto> food);
}
