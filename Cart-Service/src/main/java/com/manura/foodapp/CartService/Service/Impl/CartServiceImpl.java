package com.manura.foodapp.CartService.Service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.manura.foodapp.CartService.Service.CartService;
import com.manura.foodapp.CartService.Table.CartTable;
import com.manura.foodapp.CartService.Table.FoodTable;
import com.manura.foodapp.CartService.Table.UserTable;
import com.manura.foodapp.CartService.repo.CartRepo;
import com.manura.foodapp.CartService.repo.FoodRepo;
import com.manura.foodapp.CartService.repo.UserRepo;
import com.manura.foodapp.CartService.utils.ErrorMessages;
import com.manura.foodapp.CartService.utils.Utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import com.manura.foodapp.CartService.Controller.Req.Model.CartReq;
import com.manura.foodapp.CartService.Dto.CartDto;
import com.manura.foodapp.CartService.Dto.FoodDto;
import com.manura.foodapp.CartService.Dto.RedisCartDto;
import com.manura.foodapp.CartService.Dto.UserDto;
import com.manura.foodapp.CartService.Dto.order_service.CheckOutDto;
import com.manura.foodapp.CartService.Dto.order_service.FoodDtoProps;
import com.manura.foodapp.CartService.Error.Model.CartSerivceError;
import com.manura.foodapp.CartService.Error.Model.CartSerivceNotFoundError;

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

	@Autowired
	private RedisServiceImpl redisServiceImpl;

	@Autowired
	private Mono<RSocketRequester> rSocketRequester;

	@Override
	public Mono<String> saveCart(Mono<CartReq> cartReq, String email) {
		return cartReq.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new CartSerivceError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(i -> {
					if (email == null || email.isBlank()) {
						throw new CartSerivceError("Unauthorized");
					}
					if (i.getFood() == null) {
						throw new CartSerivceError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
					}

					return foodRepo.findByPublicId(i.getFood()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic())
							.switchIfEmpty(Mono.error(
									new CartSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.mapNotNull(food -> {
								return userRepo.findByEmail(email).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic())
										.switchIfEmpty(Mono.error(new CartSerivceNotFoundError(
												ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
										.mapNotNull(user -> {
											CartTable cartTable = cartRepo
													.findByUserNameAndFood(user.getEmail(), food.getPublicId()).block();
											if (cartTable == null) {
												CartTable cart = new CartTable();
												cart.setPublicId(utils.generateAddressId(30));
												cart.setUserName(user.getEmail());
												cart.setFood(food.getPublicId());
												cart.setUserId(user.getId());
												cart.setFoodId(food.getId());
												cart.setCount(i.getCount());
												cart.setPrice((food.getPrice() * i.getCount()));
												return cartRepo.save(cart);
											} else {
												cartTable.setCount((cartTable.getCount() + 1));
												cartTable.setPrice((cartTable.getPrice() + food.getPrice()));
												return cartRepo.save(cartTable);
											}
										}).flatMap(e -> e).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic()).map(e -> "Okay");
							}).flatMap(j -> j);
				}).flatMap(k -> k).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<Void> deleteCart(String user, String id) {
		return cartRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(
						Mono.error(new CartSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.mapNotNull(i -> {
					return userRepo.findByEmail(user).map(__ -> cartRepo.delete(i))
							.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i -> i).flatMap(i -> i).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	@Transactional
	public Mono<UserTable> saveUser(Mono<UserTable> user) {
		try {
			return user.flatMap(userRepo::save).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		} catch (Exception e) {
			return Mono.empty();
		}
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
									i.setLastModifiedDate(usr.getLastModifiedDate());
									i.setCreatedDate(usr.getCreatedDate());
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
	public Mono<FoodTable> updateFood(String id, Mono<FoodTable> food) {//
		return foodRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).mapNotNull(i -> {
					return food.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
							.mapNotNull(req -> {
								req.setId(i.getId());
								req.setPublicId(i.getPublicId());
								req.setLastModifiedDate(i.getLastModifiedDate());
								req.setCreatedDate(i.getCreatedDate());
								return req;
							}).flatMap(foodRepo::save).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i -> i).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(saveFood(Mono.just(food.block().setAsNew())));
	}

	private Flux<CartDto> cacheNotPresent(String id) {
		List<CartDto> cartDtos = new ArrayList<>();
		return cartRepo.findByUserName(id).switchIfEmpty(Flux.empty()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(e -> {
					return foodRepo.findById(e.getFoodId()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(i -> {
								return userRepo.findById(e.getUserId()).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic()).map(d -> {
											CartDto cart = new CartDto();
											cart.setId(e.getPublicId());
											cart.setFood(modelMapper.map(i, FoodDto.class));
											cart.setUser(modelMapper.map(d, UserDto.class));
											cart.setCount(e.getCount());
											cart.setPrice(e.getPrice());
											cartDtos.add(cart);
											return cart;
										});
							}).flatMap(u -> u).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic());
				}).flatMap(u -> u).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.doOnNext(i -> {
					RedisCartDto redisCartDto = new RedisCartDto();
					redisCartDto.setUser(id);
					redisCartDto.setCartDtos(cartDtos);
					redisServiceImpl.save(redisCartDto).subscribe();
				});
	}

	@Override
	public Flux<CartDto> getCart(String id) {
		return redisServiceImpl.get(id).switchIfEmpty(cacheNotPresent(id)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<UserTable> getUser(String id) {
		return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<Boolean> checkOutAll(String email) {
		return Mono.just(false).map(val -> {
			CheckOutDto checkOutDto = new CheckOutDto();
			checkOutDto.setUser(email);
			List<FoodDtoProps> cartDtos = new ArrayList<>();
			List<CartTable> userCarts = cartRepo.findByUserName(email).publishOn(Schedulers.boundedElastic())
			      .subscribeOn(Schedulers.boundedElastic()).collectList().block();
			for(CartTable i :userCarts) {
				
				FoodDtoProps props = new FoodDtoProps();
				props.setCount(i.getCount());
				props.setPrice(i.getPrice());
				props.setFoodId(i.getFood());
				cartDtos.add(props);
			}
			checkOutDto.setCartDtos(cartDtos);

			return this.rSocketRequester
					.map(rsocket -> rsocket.route("check.out.order.from.cart").data(Mono.just(checkOutDto)))
					.mapNotNull(r -> r.retrieveMono(Boolean.class)).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		}).flatMap(__ -> __).flatMap(__ -> __).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).doOnNext(i->{
					cartRepo.deleteAllByUserName(email).subscribe();
				});
	}

	@Override
	public Mono<Boolean> checkOutOne(String email, String publicId) {
		return cartRepo.findByPublicId(publicId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					CheckOutDto checkOutDto = new CheckOutDto();
					checkOutDto.setUser(email);
					List<FoodDtoProps> cartDtos = new ArrayList<>();
					FoodDtoProps props = new FoodDtoProps();
					props.setCount(i.getCount());
					props.setPrice(i.getPrice());
					props.setFoodId(i.getFood());
					cartDtos.add(props);
					return this.rSocketRequester
							.map(rsocket -> rsocket.route("check.out.order.from.cart").data(Mono.just(checkOutDto)))
							.mapNotNull(r -> r.retrieveMono(Boolean.class)).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic());
				}).flatMap(__ -> __).flatMap(__ -> __);
	}
}