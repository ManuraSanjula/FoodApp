package com.manura.foodapp.CartService.Service;

import com.manura.foodapp.CartService.Dto.CartDto;
import com.manura.foodapp.CartService.Dto.RedisCartDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RedisService {
   Mono<Void> add(String id,CartDto dto);	
   Mono<Void> save(RedisCartDto redisCartDto);
   Flux<CartDto> get(String user);
   Mono<Void> deleteCart(String user,String cartId);
   Mono<Void> deleteCart(String user);

}
