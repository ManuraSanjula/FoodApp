package com.manura.foodapp.OrderService.Controller.RSocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.manura.foodapp.OrderService.Service.Impl.OrderServiceImpl;
import com.manura.foodapp.OrderService.dto.fromCart.CheckOutDto;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller
public class RSocketController {
	
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	@MessageMapping("check.out.order.from.cart")
	public Mono<Boolean> saveOrderFromCart(@Headers Map<String, Object> metadata,@Payload Mono<CheckOutDto> cart) {
//        var email = metadata.get(Constants.FILE_EMAIL);
		return orderServiceImpl.saveOrderFromCart(cart, "").publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}
}








