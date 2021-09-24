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
import com.manura.foodapp.CartService.Controller.Res.Model.OperationStatusModel;
import com.manura.foodapp.CartService.Dto.CartDto;
import com.manura.foodapp.CartService.Error.Model.CartSerivceError;
import com.manura.foodapp.CartService.Service.Impl.CartServiceImpl;
import com.manura.foodapp.CartService.utils.ErrorMessages;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("carts")
public class CartController {
	@Autowired
	private CartServiceImpl cartServiceImpl;

	@PostMapping("/{user}")
	Mono<ResponseEntity<String>> insertCart(@PathVariable String user,@RequestBody Mono<CartReq> foodReq, Mono<Principal> principal) {

		return principal.map(Principal::getName).switchIfEmpty(Mono.just("Unauthorized"))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(usr -> {
					if(!usr.equals(user)) {
						throw new CartSerivceError(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());
					}
					return cartServiceImpl.saveCart(foodReq, usr).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(ResponseEntity::ok);
				}).flatMap(i -> i);
	}

	@GetMapping("/{user}")
	Flux<CartDto> getOneCart(@PathVariable String user,Mono<Principal> principal) {
		return cartServiceImpl.getCart(user).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@DeleteMapping("/{user}/{id}")
	Mono<ResponseEntity<Void>> deleteCart(@PathVariable String user, @PathVariable String id,Mono<Principal> principal) {
		return principal.map(Principal::getName).map(i->{
			 if(!i.equals(user)) {
					throw new CartSerivceError(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());
				}
			 return cartServiceImpl.deleteCart(user, id).map(ResponseEntity::ok).publishOn(Schedulers.boundedElastic())
						.subscribeOn(Schedulers.boundedElastic());
		 }).flatMap(__->__);
	}

	@GetMapping("/{user}/check-out-all")
	public Mono<OperationStatusModel> checkOutAll(@PathVariable String user,Mono<Principal> principal) {
		return principal.map(Principal::getName).map(u->{
			 if(!u.equals(user)) {
					throw new CartSerivceError(ErrorMessages.AUTHENTICATION_FAILED.getErrorMessage());
				}
			 return cartServiceImpl.checkOutAll(user).map(i -> {
					if (i) {
						OperationStatusModel returnValue = new OperationStatusModel();
						returnValue.setOperationName("CHECK_OUT");
						returnValue.setOperationResult("SUCCESS");
						return returnValue;
					} else {
						OperationStatusModel returnValue = new OperationStatusModel();
						returnValue.setOperationName("CHECK_OUT");
						returnValue.setOperationResult("FAIL");
						return returnValue;
					}
				}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
		 }).flatMap(__->__);
		 
	}

}
