/**
 * 
 */
package com.manura.foodapp.CartService.Service.Impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;

import com.manura.foodapp.CartService.Dto.CartDto;
import com.manura.foodapp.CartService.Dto.RedisCartDto;
import com.manura.foodapp.CartService.Service.RedisService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author manurasanjula
 *
 */
public class RedisServiceImpl implements RedisService {

	@Autowired
	private ReactiveRedisTemplate<String, RedisCartDto> reactiveRedisTemplateForCart;
	private ReactiveValueOperations<String, RedisCartDto> reactiveRedisTemplateOpsCart;
	
	@PostConstruct
	public void setup() {
		reactiveRedisTemplateOpsCart = reactiveRedisTemplateForCart.opsForValue();
	}
	
	@Override
	public Mono<Void> save(RedisCartDto redisCartDto) {
		 try {
			 reactiveRedisTemplateOpsCart.set(redisCartDto.getUser(), redisCartDto).
			 publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).subscribe();
		 }catch (Exception e) {
			// TODO: handle exception
		}
		 return Mono.empty();
	}

	@Override
	public Flux<CartDto> get(String user) {
		 try {
			 return reactiveRedisTemplateOpsCart.get(user).flatMapMany(i-> Flux.fromIterable(i.getCartDtos()))
					 .switchIfEmpty(Flux.empty())
					 .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
		 }catch (Exception e) {
			 return Flux.empty();
		}
	}

	@Override
	public Mono<Void> deleteCart(String user, String cartId) {
		try {
			reactiveRedisTemplateOpsCart.get(user).map(i->{
				List<CartDto> cartDtos = new ArrayList<>();
				cartDtos.addAll(i.getCartDtos());
				cartDtos.removeIf(d->d.getUser().equals(user));
				i.setCartDtos(cartDtos);
				return i;
			}).map(i->{
				 return reactiveRedisTemplateOpsCart.set(i.getUser(),i);
			}).flatMap(i->i).subscribe();
			 return Mono.empty();
		}catch (Exception e) {
			 return Mono.empty();
		}
	}

}
