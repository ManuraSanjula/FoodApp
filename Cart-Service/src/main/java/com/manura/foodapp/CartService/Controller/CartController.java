package com.manura.foodapp.CartService.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manura.foodapp.CartService.Controller.Req.Model.CartReq;
import com.manura.foodapp.CartService.Service.Impl.CartServiceImpl;
import com.manura.foodapp.CartService.Table.Dto.CartDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("cart")
public class CartController {
  @Autowired
  private CartServiceImpl cartServiceImpl;
  
   @PostMapping
	Mono<ResponseEntity<CartDto>> insertCart(@RequestBody Mono<CartReq> foodReq) {
		return cartServiceImpl.saveCart(foodReq).map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.badRequest().build())
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
   
    @GetMapping("/{user}")
  	Flux<CartDto> getOneCart(@PathVariable String user) {
  		return cartServiceImpl.getCart(user)
  				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
  	}
  
}
