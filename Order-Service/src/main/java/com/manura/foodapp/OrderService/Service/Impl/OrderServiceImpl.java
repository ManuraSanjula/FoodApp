/**
 * 
 */
package com.manura.foodapp.OrderService.Service.Impl;



import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manura.foodapp.OrderService.Service.OrderService;
import com.manura.foodapp.OrderService.Table.FoodTable;
import com.manura.foodapp.OrderService.Table.OrderTable;
import com.manura.foodapp.OrderService.Table.UserTable;
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
			return user.flatMap(userRepo::save).publishOn(Schedulers.boundedElastic())
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
		return food.flatMap(foodRepo::save).publishOn(Schedulers.boundedElastic())
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
	public Mono<String> saveCart(Mono<OrderReq> cart, String email) {
		return cart.mapNotNull(i->{
			return foodRepo.findByPublicId(i.getFood()).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).mapNotNull(food->{
						return userRepo.findByEmail(email).map(user->{
							OrderTable orderTable = new OrderTable();
							orderTable.setPublicId(utils.generateAddressId(30));
							orderTable.setUserName(user.getEmail());
							orderTable.setFood(food.getPublicId());
							orderTable.setUserId(user.getId());
							orderTable.setFoodId(food.getId());
							orderTable.setCount(i.getCount());
							orderTable.setPrice((food.getPrice() * i.getCount()));
							orderTable.setItem(food);
							orderTable.setOwner(user);
							return orderTable;
						}).flatMap(orderRepo::save).mapNotNull(__->"Okay");
					}).flatMap(__->__);
		}).flatMap(i->i);
	}

	
	@Override
	public Flux<OrderDto> getCart(String id) {
		return orderRepo.findByUserName(id).switchIfEmpty(Flux.empty()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(e -> {
					return foodRepo.findById(e.getFoodId()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(i -> {
								return userRepo.findById(e.getUserId()).publishOn(Schedulers.boundedElastic())
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
				}).flatMap(u -> u).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
	
}