/**
 * 
 */
package com.manura.foodapp.OrderService.Service.Impl;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manura.foodapp.OrderService.Error.Model.OrderSerivceNotFoundError;
import com.manura.foodapp.OrderService.Service.OrderService;
import com.manura.foodapp.OrderService.Table.FoodTable;
import com.manura.foodapp.OrderService.Table.OrderTable;
import com.manura.foodapp.OrderService.Table.UserTable;
import com.manura.foodapp.OrderService.Utils.ErrorMessages;
import com.manura.foodapp.OrderService.Utils.Utils;
import com.manura.foodapp.OrderService.controller.Req.OrderReq;
import com.manura.foodapp.OrderService.dto.FoodDto;
import com.manura.foodapp.OrderService.dto.OrderDto;
import com.manura.foodapp.OrderService.dto.UserDto;
import com.manura.foodapp.OrderService.repo.FoodRepo;
import com.manura.foodapp.OrderService.repo.OrderRepo;
import com.manura.foodapp.OrderService.repo.UserRepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author manurasanjula
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
    private OrderRepo orderRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private FoodRepo foodRepo;
	@Autowired
	private Utils utils;
	
	private ModelMapper modelMapper = new ModelMapper();
	
	@Override
	public Mono<UserTable> saveUser(Mono<UserTable> user) {
		try {
			return user.doOnNext(i->{
				UUID uuid = UUID.randomUUID();
				Long id = (long) (uuid.variant()+uuid.version());
				i.setId(id);
			}).flatMap(userRepo::save).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		} catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<UserTable> getUser(String id) {
		return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<UserTable> updateUser(String id, Mono<UserTable> user) {
		try {
			return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).mapNotNull(usr -> {
						return user.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
								.mapNotNull(i -> {
									i.setId(usr.getId());
									i.setPublicId(usr.getPublicId());
									return i;
								}).flatMap(userRepo::save).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic());
					}).flatMap(i -> i).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.switchIfEmpty(saveUser(user));
		} catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<FoodTable> saveFood(Mono<FoodTable> food) {
		return food.doOnNext(i->{
			UUID uuid = UUID.randomUUID();
			Long id = (long) (uuid.variant()+uuid.version());
			i.setId(id);
		}).flatMap(foodRepo::save).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodTable> updateFood(String id, Mono<FoodTable> food) {
		return foodRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
		.subscribeOn(Schedulers.boundedElastic()).mapNotNull(i -> {
			return food.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.mapNotNull(req -> {
						req.setId(i.getId());
						req.setPublicId(i.getPublicId());
						return req;
					}).flatMap(foodRepo::save).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		}).flatMap(i -> i).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
		.switchIfEmpty(saveFood(food));
	}

	@Override
	public Mono<String> saveOrder(Mono<OrderReq> cart, String email) {
		return cart
				.switchIfEmpty(Mono.error(new OrderSerivceNotFoundError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(i->{
			return foodRepo.findByPublicId(i.getFood())
					.switchIfEmpty(Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
					.publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).mapNotNull(food->{
						return userRepo.findByEmail(email)
								.switchIfEmpty(Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
								.mapNotNull(user->{
							OrderTable orderTable = new OrderTable();
							orderTable.setAddress(user.getAddress());
							orderTable.setPublicId(utils.generateAddressId(30));
							orderTable.setUserName(user.getEmail());
							orderTable.setFood(food.getPublicId());
							orderTable.setCount(i.getCount());
							orderTable.setPrice((food.getPrice() * i.getCount()));
							orderTable.setStatus("processing");
							orderTable.setTracking_Number(utils.generateAddressId(20));
							return orderTable;
						}).doOnNext(o->{
							UUID uuid = UUID.randomUUID();
							Long id = (long) (uuid.variant()+uuid.version());
							o.setId(id);
						}).flatMap(orderRepo::save).doOnNext(d->{
							
						}).mapNotNull(__->"Okay");
					}).flatMap(__->__);
		}).flatMap(i->i);
	}

	
	@Override
	public Flux<OrderDto> getOrder(String id) {
		return orderRepo.findByUserName(id).switchIfEmpty(Flux.empty()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(e -> {
					return foodRepo.findByPublicId(e.getFood()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(i -> {
								return userRepo.findByPublicId(e.getUserName()).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic()).map(d -> {
											OrderDto cart = new OrderDto();
											cart.setId(e.getPublicId());
											cart.setFood(modelMapper.map(i, FoodDto.class));
											cart.setUser(modelMapper.map(d, UserDto.class));
											cart.setCount(e.getCount());
											cart.setPrice(e.getPrice());
											return cart;
										});
							}).flatMap(u -> u).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic());
				}).flatMap(u -> u).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Flux.empty());
	}

	@Override
	public Mono<String> confirmOrder(String id, String userId) {
		// TODO Auto-generated method stub
		return null;
	}
	
}