package com.manura.foodapp.OrderService.Service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;

import com.manura.foodapp.OrderService.Error.Model.OrderSerivceNotFoundError;
import com.manura.foodapp.OrderService.Service.OrderService;
import com.manura.foodapp.OrderService.Table.FoodTable;
import com.manura.foodapp.OrderService.Table.OrderTable;
import com.manura.foodapp.OrderService.Table.TrackingDetailsTable;
import com.manura.foodapp.OrderService.Table.UserTable;
import com.manura.foodapp.OrderService.Table.RefundTable;

import com.manura.foodapp.OrderService.Utils.ErrorMessages;
import com.manura.foodapp.OrderService.Utils.Utils;
import com.manura.foodapp.OrderService.controller.Req.OrderReq;
import com.manura.foodapp.OrderService.dto.FoodDto;
import com.manura.foodapp.OrderService.dto.FullOrderDto;
import com.manura.foodapp.OrderService.dto.OrderDto;
import com.manura.foodapp.OrderService.dto.RefundDto;
import com.manura.foodapp.OrderService.dto.TrackingDetailsDto;
import com.manura.foodapp.OrderService.dto.UserDto;
import com.manura.foodapp.OrderService.repo.FoodRepo;
import com.manura.foodapp.OrderService.repo.OrderRepo;
import com.manura.foodapp.OrderService.repo.RefundRepo;
import com.manura.foodapp.OrderService.repo.TrackingDetailsRepo;
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
	private TrackingDetailsRepo trackingDetailsRepo;
	@Autowired
	private Utils utils;
	@Autowired
	private RefundRepo refundRepo;
	@Autowired
	private Mono<RSocketRequester> rSocketRequester;
	private ModelMapper modelMapper = new ModelMapper();

	@Override
	public Mono<UserTable> saveUser(Mono<UserTable> user) {
		try {
			return user.doOnNext(i -> {
				UUID uuid = UUID.randomUUID();
				Long id = (long) (uuid.variant() + uuid.version());
				i.setId(id);
			}).flatMap(userRepo::save).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
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
		return food.doOnNext(i -> {
			UUID uuid = UUID.randomUUID();
			Long id = (long) (uuid.variant() + uuid.version());
			i.setId(id);
		}).flatMap(foodRepo::save).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
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
				.switchIfEmpty(Mono
						.error(new OrderSerivceNotFoundError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(i -> {
					return foodRepo.findByPublicId(i.getFood())
							.switchIfEmpty(Mono.error(
									new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
							.mapNotNull(food -> {
								return userRepo.findByEmail(email).switchIfEmpty(Mono.error(
										new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
										.mapNotNull(user -> {
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
										}).doOnNext(o -> {
											UUID uuid = UUID.randomUUID();
											Long id = (long) (uuid.variant() + uuid.version());
											o.setId(id);
										}).flatMap(orderRepo::save).doOnNext(d -> {
											TrackingDetailsTable trackingDetailsTable = new TrackingDetailsTable();
											trackingDetailsTable.setUserId(email);
											trackingDetailsTable.setOrderId(d.getPublicId());
											trackingDetailsTable.setDeliveryStatus("Not Delivered");
											UUID uuid = UUID.randomUUID();
											Long id = (long) (uuid.variant() + uuid.version());
											trackingDetailsTable.setId(id);
											trackingDetailsRepo.save(trackingDetailsTable).subscribe();
										}).mapNotNull(__ -> "Okay");
							}).flatMap(__ -> __);
				}).flatMap(i -> i);
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
		return orderRepo.findByUserNameAndFood(userId, id)
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.doOnNext(i -> i.setStatus("delivered successfully")).flatMap(orderRepo::save)
				.map(i -> "Thank you for Ordering").publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FullOrderDto> getOneOrder(String userId, String orderId) {
		return orderRepo.findByUserNameAndFood(userId, orderId).publishOn(Schedulers.boundedElastic())
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.subscribeOn(Schedulers.boundedElastic()).mapNotNull(order -> {
					return trackingDetailsRepo.findByOrderId(order.getId()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).mapNotNull(tracking -> {
								return foodRepo.findByPublicId(order.getFood()).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic()).mapNotNull(food -> {
											return userRepo.findByEmail(order.getUserName())
													.publishOn(Schedulers.boundedElastic())
													.subscribeOn(Schedulers.boundedElastic()).mapNotNull(user -> {
														FullOrderDto fullOrderDto = new FullOrderDto();
														fullOrderDto.setId(order.getPublicId());
														fullOrderDto.setFood(modelMapper.map(food, FoodDto.class));
														fullOrderDto.setUser(modelMapper.map(user, UserDto.class));
														fullOrderDto.setCount(order.getCount());
														fullOrderDto.setPrice(order.getPrice());
														fullOrderDto.setTrackingDetails(
																modelMapper.map(tracking, TrackingDetailsDto.class));
														return fullOrderDto;
													});
										}).flatMap(__ -> __);
							}).flatMap(__ -> __);
				}).flatMap(__ -> __).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());

	}

	@Override
	public Flux<RefundDto> getAllRefund(String userId) {
		return refundRepo.findByUserId(userId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					return orderRepo.findByPublicId(i.getOrderId()).map(order -> {
						return foodRepo.findByPublicId(order.getFood()).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).mapNotNull(food -> {
									FoodDto foodDto = modelMapper.map(food, FoodDto.class);
									RefundDto refundDto = modelMapper.map(i, RefundDto.class);
									OrderDto orderDto = modelMapper.map(order, OrderDto.class);
									orderDto.setFood(foodDto);
									refundDto.setOrder(orderDto);
									return refundDto;
								});
					});
				}).flatMap(__ -> __).flatMap(__ -> __);
	}

	@Override
	public Mono<RefundDto> requestARefund(Flux<FilePart> filePartFlux, String email, String reason, String userId,
			String orderId) {
		List<String> images = new ArrayList<>();
		return Mono.just(new RefundTable()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					filePartFlux.map(file -> {
						String image = this.rSocketRequester
								.map(rsocket -> rsocket.route("file.upload.refund").data(file.content()))
								.mapNotNull(r -> r.retrieveFlux(String.class)).flatMapMany(s -> s).distinct()
								.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
								.blockFirst();
						return image;
					}).subscribe(images::add);

					UUID uuid = UUID.randomUUID();
					Long id = (long) (uuid.variant() + uuid.version());
					i.setId(id);
					i.setPublicId(utils.generateId(20));
					i.setReason(reason);
					i.setDate(new Date());
					i.setOrderId(orderId);
					i.setUserId(userId);
					i.setSuccess(false);
					i.setStatus("Pending");
					return i;
				}).map(i -> {
					i.setEvidence(images);
					return i;
				}).flatMap(refundRepo::save).map(i -> modelMapper.map(i, RefundDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());

	}
}
