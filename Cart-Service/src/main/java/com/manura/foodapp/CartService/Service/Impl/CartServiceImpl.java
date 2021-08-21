package com.manura.foodapp.CartService.Service.Impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manura.foodapp.CartService.Service.CartService;
import com.manura.foodapp.CartService.Table.FoodTable;
import com.manura.foodapp.CartService.Table.UserTable;
import com.manura.foodapp.CartService.Table.Dto.CartDto;
import com.manura.foodapp.CartService.repo.CartRepo;
import com.manura.foodapp.CartService.repo.FoodRepo;
import com.manura.foodapp.CartService.repo.UserRepo;
import com.manura.foodapp.CartService.utils.Utils;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author manurasanjula
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepo cartRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private FoodRepo foodRepo;
	
	private ModelMapper modelMapper = new ModelMapper();
	
	@Autowired
	private Utils utils;
	
	@Override
	public Mono<CartDto> saveCart(Mono<CartDto> cart) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Void> deleteCart(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public Mono<UserTable> saveUser(Mono<UserTable> user) {
		try {
			return user.map(i->modelMapper.map(i, UserTable.class))
					.flatMap(userRepo::save)
					.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
		}catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<UserTable> updateUser(String id,Mono<UserTable> user) {
		try {
			return userRepo.findByPublicId(id).doOnNext(i->{
				if(i == null) {
					i.setActive(true);
				}
			}).switchIfEmpty(saveUser(user)).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).mapNotNull(usr -> {
						return user.publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
								.subscribeOn(Schedulers.boundedElastic()).mapNotNull(i -> {
									if (i.getEmail() != null) {
										usr.setEmail(i.getEmail());
									}
									if (i.getFirstName() != null) {
										usr.setFirstName(i.getFirstName());
									}
									if (i.getLastName() != null) {
										usr.setLastName(i.getLastName());
									}
									if (i.getAddress() != null) {
										usr.setAddress(i.getAddress());
									}
									if (i.getPic() != null) {
										usr.setPic(i.getPic());
									}
									if (i.getEmailVerify() != null) {
										usr.setEmailVerify(i.getEmailVerify());
									}
									if (i.getActive() != null) {
										usr.setActive(i.getActive());
									}
									return usr;
								}).flatMap(userRepo::save).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic());
					}).flatMap(i -> i).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());

		}catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<FoodTable> saveFood(Mono<FoodTable> food) {
		return food
				.flatMap(foodRepo::save)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodTable> updateFood(String id,Mono<FoodTable> food) {
		return foodRepo.findByPublicId(id).switchIfEmpty(saveFood(food)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i->{
					return food.publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).mapNotNull(req -> {
						req.setId(i.getId());
						req.setPublicId(i.getPublicId());
						return req;
					}).flatMap(foodRepo::save)
							.publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i->i)
				.publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

}