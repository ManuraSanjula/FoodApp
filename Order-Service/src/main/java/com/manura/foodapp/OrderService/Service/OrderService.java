/**
 * 
 */
package com.manura.foodapp.OrderService.Service;


import com.manura.foodapp.OrderService.Table.FoodTable;
import com.manura.foodapp.OrderService.Table.UserTable;
import com.manura.foodapp.OrderService.controller.Req.OrderReq;
import com.manura.foodapp.OrderService.dto.OrderDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
public interface OrderService {
    Mono<UserTable> saveUser(Mono<UserTable> user);
	
    Mono<String> saveCart(Mono<OrderReq> cart,String email);
	Flux<OrderDto> getCart(String id);
	
	Mono<UserTable> getUser(String id);

	Mono<UserTable> updateUser(String id,Mono<UserTable> user);
	
	Mono<FoodTable> saveFood(Mono<FoodTable> food);

	Mono<FoodTable> updateFood(String id,Mono<FoodTable> food);
}
