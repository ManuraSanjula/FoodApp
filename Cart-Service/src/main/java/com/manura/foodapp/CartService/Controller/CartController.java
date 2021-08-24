package com.manura.foodapp.CartService.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	Mono<ResponseEntity<String>> insertCart(@RequestBody Mono<CartReq> foodReq,Mono<Principal> principal) {
    	
    	return principal.map(Principal::getName).switchIfEmpty(Mono.just("Unauthorized"))
    			.publishOn(Schedulers.boundedElastic())
		     .subscribeOn(Schedulers.boundedElastic()).map(usr->{
		    	 return cartServiceImpl.saveCart(foodReq,usr)
		    			 .publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
		    			 .map(ResponseEntity::ok);
		     }).flatMap(i->i);
	}
   
    @GetMapping("/{user}")
  	Flux<CartDto> getOneCart(@PathVariable String user) {
  		return cartServiceImpl.getCart(user)
  				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
  	}
    
    @DeleteMapping("/{user}/{id}")
   	Mono<ResponseEntity<Void>> deleteCart(@PathVariable String user,@PathVariable String id) {
   		return cartServiceImpl.deleteCart(user, id).map(ResponseEntity::ok)
   				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
   	}
 
}
