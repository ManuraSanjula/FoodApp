package com.manura.foodapp.OrderService.Service;

import org.springframework.http.codec.multipart.FilePart;

import com.manura.foodapp.OrderService.Table.FoodTable;
import com.manura.foodapp.OrderService.Table.OrderTable;
import com.manura.foodapp.OrderService.Table.UserTable;
import com.manura.foodapp.OrderService.controller.Req.BillingAndDeliveryAddressReq;
import com.manura.foodapp.OrderService.controller.Req.OrderReq;
import com.manura.foodapp.OrderService.dto.BillingAndDeliveryAddressDto;
import com.manura.foodapp.OrderService.dto.CartDto;
import com.manura.foodapp.OrderService.dto.FullOrderDto;
import com.manura.foodapp.OrderService.dto.OrderDto;
import com.manura.foodapp.OrderService.dto.RefundDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author manurasanjula
 *
 */
public interface OrderService {
    Mono<UserTable> saveUser(Mono<UserTable> user);
    Mono<Boolean> saveOrder(Mono<OrderReq> cart,String email);
    Flux<Boolean> saveManyOrderFromCart(Flux<CartDto> cart,String email);
    Mono<Boolean> saveOrderFromCart(Mono<CartDto> cart,String email);

	Flux<OrderDto> getOrder(String id);
	Mono<FullOrderDto> getOneOrder(String userId,String orderId);
	Mono<UserTable> getUser(String id);
	Mono<UserTable> updateUser(String id,Mono<UserTable> user);
	Mono<FoodTable> saveFood(Mono<FoodTable> food);
	Mono<FoodTable> updateFood(String id,Mono<FoodTable> food);
	
	Mono<Boolean> orderCompleted(String userId,String orderId);
	
	Mono<Boolean> orderAccepted(String userId,String orderId);

	
	Mono<Boolean> confirmOrder(String userId,String orderId);

	Mono<Boolean> requestARefund(Flux<FilePart> filePartFlux,
			String email,
			String reason,String orderId); 
	Flux<RefundDto> getAllRefund(String userId);
	Mono<Boolean> setNewBillingAndDeliveryAddress(Mono<BillingAndDeliveryAddressReq> req,String user);
	Flux<BillingAndDeliveryAddressDto> getAllBillingAndDeliveryAddress(String user);
	Mono<Boolean> changeBillingAndDeliveryAddress(String user,Long billingId);
	void Send_OrderInformation_Email_And_PDF(Mono<OrderTable> order,String email,String orderId);

}