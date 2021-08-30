package com.manura.foodapp.OrderService.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.manura.foodapp.OrderService.Service.Impl.OrderServiceImpl;
import com.manura.foodapp.OrderService.controller.Req.OrderReq;
import com.manura.foodapp.OrderService.dto.OrderDto;
import com.manura.foodapp.OrderService.dto.RefundDto;
import com.manura.foodapp.OrderService.dto.FullOrderDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
	private OrderServiceImpl orderServiceImpl;

	@PostMapping
	public Mono<ResponseEntity<String>> saveOrder(@RequestBody Mono<OrderReq> req, Mono<Principal> principal) {
		return principal.map(Principal::getName).switchIfEmpty(Mono.just("Unauthorized"))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(usr -> {
					return orderServiceImpl.saveOrder(req, usr).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(ResponseEntity::ok);
				}).flatMap(i -> i);
	}

	@GetMapping("/{email}")
	public Flux<OrderDto> allOrders(@PathVariable String email) {
		return orderServiceImpl.getOrder(email).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@GetMapping("/{email}/{orderId}")
	public Mono<FullOrderDto> getOneOrder(@PathVariable String email, @PathVariable String orderId) {
		return orderServiceImpl.getOneOrder(email, orderId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@GetMapping("/{email}/{orderId}")
	public Mono<String> confirmOrder(@PathVariable String email, @PathVariable String orderId) {
		return orderServiceImpl.confirmOrder(email, orderId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@GetMapping("/{email}/refund")
	public Flux<RefundDto> getAllRefund(@PathVariable String email) {
		return orderServiceImpl.getAllRefund(email).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@PostMapping("/{email}/refund")
	public Mono<RefundDto> requestARefund(@RequestPart("email") String email, @RequestPart("reason") String reason,
			@RequestPart("userId") String userId, @RequestPart("orderId") String orderId,
			@RequestPart(name = "images", required = false) Flux<FilePart> fileParts) {
		return orderServiceImpl.requestARefund(fileParts, email, reason, userId, orderId)
				.publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}
}
