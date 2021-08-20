package com.manura.foodapp.CartService.Service.Impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manura.foodapp.CartService.Service.CartService;
import com.manura.foodapp.CartService.Table.Dto.CartDto;
import com.manura.foodapp.CartService.Table.Dto.FoodDto;
import com.manura.foodapp.CartService.Table.Dto.UserDto;
import com.manura.foodapp.CartService.repo.CartRepo;
import com.manura.foodapp.CartService.repo.FoodRepo;
import com.manura.foodapp.CartService.repo.UserRepo;
import com.manura.foodapp.CartService.utils.Utils;

import reactor.core.publisher.Mono;

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
	public Mono<UserDto> saveUser(Mono<UserDto> user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<UserDto> updateUser(Mono<UserDto> user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<FoodDto> saveFood(Mono<FoodDto> food) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<FoodDto> updateFood(Mono<FoodDto> food) {
		// TODO Auto-generated method stub
		return null;
	}

}
