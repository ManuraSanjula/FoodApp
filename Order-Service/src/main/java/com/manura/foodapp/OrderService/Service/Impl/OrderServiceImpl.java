package com.manura.foodapp.OrderService.Service.Impl;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.manura.foodapp.OrderService.Error.Model.OrderSerivceNotFoundError;
import com.manura.foodapp.OrderService.Service.OrderService;
import com.manura.foodapp.OrderService.Table.BillingAndDeliveryAddressTable;
import com.manura.foodapp.OrderService.Table.FoodTable;
import com.manura.foodapp.OrderService.Table.OrderTable;
import com.manura.foodapp.OrderService.Table.TrackingDetailsTable;
import com.manura.foodapp.OrderService.Table.UserTable;
import com.manura.foodapp.OrderService.Table.Support.OrderFoodInfromation;
import com.manura.foodapp.OrderService.Table.RefundTable;

import com.manura.foodapp.OrderService.Utils.ErrorMessages;
import com.manura.foodapp.OrderService.Utils.TokenCreator;
import com.manura.foodapp.OrderService.Utils.Utils;
import com.manura.foodapp.OrderService.controller.Req.BillingAndDeliveryAddressReq;
import com.manura.foodapp.OrderService.controller.Req.OrderReq;
import com.manura.foodapp.OrderService.dto.BillingAndDeliveryAddressDto;
import com.manura.foodapp.OrderService.dto.CartDto;
import com.manura.foodapp.OrderService.dto.FoodDto;
import com.manura.foodapp.OrderService.dto.FoodInfoDto;
import com.manura.foodapp.OrderService.dto.FullOrderDto;
import com.manura.foodapp.OrderService.dto.OrderDto;
import com.manura.foodapp.OrderService.dto.RefundDto;
import com.manura.foodapp.OrderService.dto.TrackingDetailsDto;
import com.manura.foodapp.OrderService.dto.UserDto;
import com.manura.foodapp.OrderService.repo.BillingAndDeliveryAddressRepo;
import com.manura.foodapp.OrderService.repo.FoodRepo;
import com.manura.foodapp.OrderService.repo.OrderRepo;
import com.manura.foodapp.OrderService.repo.RefundRepo;
import com.manura.foodapp.OrderService.repo.TrackingDetailsRepo;
import com.manura.foodapp.OrderService.repo.UserRepo;
import com.nimbusds.jwt.SignedJWT;

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
	private BillingAndDeliveryAddressRepo billingAndDeliveryAddressRepo;
	@Autowired
	private Mono<RSocketRequester> rSocketRequester;
	private ModelMapper modelMapper = new ModelMapper();
	@Autowired
	private SpringTemplateEngine thymeleafTemplateEngine;
	final String FROM = "w.m.manurasanjula12345@gmail.com";
	@Autowired
	private RedisServiceImpl redisServiceImpl;
	@Autowired
	private TokenCreator tokenCreator;

	@Override
	public Mono<UserTable> saveUser(Mono<UserTable> user) {
		try {
			return user.doOnNext(i -> {
				int min = 10;
				long max = 100000000000000000L;
				Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);
				i.setId(random_int);
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
					}).flatMap(__ -> __).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.switchIfEmpty(saveUser(user));
		} catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public Mono<FoodTable> saveFood(Mono<FoodTable> food) {
		return food.doOnNext(i -> {
			int min = 10;
			long max = 100000000000000000L;
			Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);
			i.setId(random_int);
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
				}).flatMap(__ -> __).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(saveFood(food));
	}

	@Override
	public Mono<Boolean> saveOrder(Mono<OrderReq> cart, String email) {
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
											if (user.getBillingAndDeliveryAddress() == null) {
												throw new OrderSerivceNotFoundError(
														"Any BillingAdress DeliveryAdress not Found ");
											}
											return billingAndDeliveryAddressRepo
													.findById(user.getBillingAndDeliveryAddress())
													.publishOn(Schedulers.boundedElastic())
													.subscribeOn(Schedulers.boundedElastic())
													.switchIfEmpty(Mono.error(new OrderSerivceNotFoundError(
															ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
													.mapNotNull(billing -> {
														List<OrderFoodInfromation> foodsInfo = new ArrayList<>();
														OrderTable orderTable = new OrderTable();
														orderTable.setAddress(user.getAddress());
														orderTable.setPublicId(utils.generateAddressId(30));
														orderTable.setUserName(user.getEmail());
														OrderFoodInfromation orderFoodInfromation = new OrderFoodInfromation();
														orderFoodInfromation.setFoodId(food.getPublicId());
														orderFoodInfromation.setCount(i.getCount());
														orderFoodInfromation.setPrice((food.getPrice() * i.getCount()));
														foodsInfo.add(orderFoodInfromation);
														orderTable.setFoodsInfo(foodsInfo);
														orderTable.setStatus("processing");
														orderTable.setTrackingNumber(utils.generateAddressId(20));
														orderTable.setBillingAndDeliveryAddress(billing.getId());
														Double totalPrice = 0D;
														for (OrderFoodInfromation info : foodsInfo) {
															totalPrice = (totalPrice + info.getPrice());
														}
														orderTable.setTotalPrice(totalPrice);
														return orderTable;
													});
										}).flatMap(__ -> __).doOnNext(o -> {
											int min = 10;
											long max = 100000000000000000L;
											Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);
											o.setId(random_int);
										}).flatMap(orderRepo::save).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic()).doOnNext(d -> {
											TrackingDetailsTable trackingDetailsTable = new TrackingDetailsTable();
											trackingDetailsTable.setUserId(email);
											trackingDetailsTable.setOrderId(d.getPublicId());
											trackingDetailsTable.setDeliveryStatus("Not Delivered");
											int min = 10;
											long max = 100000000000000000L;
											Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);
											trackingDetailsTable.setId(random_int);
											trackingDetailsRepo.save(trackingDetailsTable).subscribe();
											Runnable orderInformation = () -> Send_OrderInformation_Email_And_PDF(
													Mono.just(d), d.getUserName(), d.getPublicId());
											new Thread(orderInformation).start();
										}).mapNotNull(__ -> true);
							}).flatMap(__ -> __);
				}).flatMap(__ -> __).switchIfEmpty(Mono.just(false)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Flux<OrderDto> getOrder(String id) {
		return orderRepo.findByUserName(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(e -> {
					return userRepo.findByEmail(e.getUserName()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(user -> {
								return Flux.fromIterable(e.getFoodsInfo()).map(foodInfo -> {
									return foodRepo.findByPublicId(foodInfo.getFoodId()).map(foodTable -> {
										List<FoodDto> food = new ArrayList<>();
										OrderDto cart = new OrderDto();
										cart.setId(e.getPublicId());
										cart.setTotalPrice(e.getTotalPrice());

										FoodInfoDto foodInfoDto = new FoodInfoDto();
										foodInfoDto.setCount(foodInfo.getCount());
										foodInfoDto.setPrice(foodInfo.getPrice());

										FoodDto foodDto = modelMapper.map(foodTable, FoodDto.class);
										foodDto.setFoodInfo(foodInfoDto);
										food.add(foodDto);
										cart.setFood(food);
										return cart;
									});
								}).flatMap(__ -> __);
							});
				}).flatMap(__ -> __).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.flatMap(__ -> __);
	}

	@Override
	public Mono<Boolean> confirmOrder(String userId, String orderId) {
		return orderRepo.findByPublicId(orderId)
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.doOnNext(i -> {
					i.setStatus("Ordering conformation successfully");
					i.setOrderRecive(true);
				}).flatMap(orderRepo::save).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					return userRepo.findByEmail(userId)
							.switchIfEmpty(Mono.error(
									new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.map(user -> {
								Map<String, Object> data = new HashMap<>();
								Context thymeleafContext = new Context();
								String text = "Your" + " " + i.getPublicId() + "order is Accepted!";
								data.put("order", text);
								thymeleafContext.setVariables(data);
								String htmlBody = thymeleafTemplateEngine.process("OrderConfirmation",
										thymeleafContext);
								sendMail(user.getEmail(), htmlBody);
								return Mono.just(true);
							});
				}).flatMap(__ -> __).flatMap(__ -> __).switchIfEmpty(Mono.just(false));
	}

	@Override
	public Mono<FullOrderDto> getOneOrder(String userId, String orderId) {
		return orderRepo.findByPublicId(orderId).publishOn(Schedulers.boundedElastic())
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.subscribeOn(Schedulers.boundedElastic()).map(order -> {
					return trackingDetailsRepo.findByOrderId(order.getPublicId()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(trackingD -> {
								return Flux.fromIterable(order.getFoodsInfo()).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic()).map(info -> {
											return foodRepo.findByPublicId(info.getFoodId())
													.publishOn(Schedulers.boundedElastic())
													.subscribeOn(Schedulers.boundedElastic()).map(food -> {
														return userRepo.findByEmail(userId)
																.publishOn(Schedulers.boundedElastic())
																.subscribeOn(Schedulers.boundedElastic()).map(user -> {
																	List<FoodDto> foodDtos = new ArrayList<>();

																	FoodInfoDto foodInfoDto = new FoodInfoDto();
																	foodInfoDto.setCount(info.getCount());
																	foodInfoDto.setPrice(info.getPrice());

																	FoodDto foodDto = modelMapper.map(food,
																			FoodDto.class);
																	foodDto.setFoodInfo(foodInfoDto);
																	foodDtos.add(foodDto);
																	FullOrderDto fullOrderDto = new FullOrderDto();
																	fullOrderDto.setUser(
																			modelMapper.map(user, UserDto.class));
																	fullOrderDto.setTrackingDetails(modelMapper
																			.map(trackingD, TrackingDetailsDto.class));
																	fullOrderDto.setId(order.getPublicId());
																	fullOrderDto.setTotalPrice(order.getTotalPrice());
																	fullOrderDto.setFood(foodDtos);
																	return fullOrderDto;
																});
													}).flatMap(__ -> __);
										});
							}).flatMapMany(__ -> __);
				}).flatMapMany(__ -> __).distinct().blockLast();
	}

	@Override
	public Flux<RefundDto> getAllRefund(String userId) {
		return refundRepo.findByUserId(userId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(i -> {
					return orderRepo.findByPublicId(i.getOrderId()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(order -> {
								List<FoodDto> foodDtos = new ArrayList<>();
								return Flux.fromIterable(order.getFoodsInfo()).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic()).flatMap(info -> {
											return foodRepo.findByPublicId(info.getFoodId())
													.publishOn(Schedulers.boundedElastic())
													.subscribeOn(Schedulers.boundedElastic()).doOnNext(h -> {
														FoodDto foodDto = modelMapper.map(h, FoodDto.class);
														foodDtos.add(foodDto);
													}).map(food -> {
														RefundDto refundDto = modelMapper.map(i, RefundDto.class);
														OrderDto orderDto = modelMapper.map(order, OrderDto.class);
														orderDto.setFood(foodDtos);
														refundDto.setOrder(orderDto);
														return refundDto;
													});
										});
							});
				}).flatMap(__ -> __).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<Boolean> requestARefund(Flux<FilePart> filePartFlux, String email, String reason, String orderId) {
		List<String> images = new ArrayList<>();
		return Mono.just(new RefundTable()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(i -> {
					return userRepo.findByEmail(email)
							.switchIfEmpty(Mono.error(
									new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.flatMap(user -> {
								return orderRepo.findByPublicId(orderId).switchIfEmpty(Mono.error(
										new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
										.map(order -> {
											filePartFlux.map(file -> {
												String image = this.rSocketRequester
														.map(rsocket -> rsocket.route("file.upload.refund")
																.data(file.content()))
														.mapNotNull(r -> r.retrieveFlux(String.class))
														.flatMapMany(s -> s).distinct()
														.publishOn(Schedulers.boundedElastic())
														.subscribeOn(Schedulers.boundedElastic()).blockFirst();
												return ("/refund-image/" + image);
											}).subscribe(images::add);
											int min = 10;
											long max = 100000000000000000L;
											Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);
											i.setId(random_int);
											i.setPublicId(utils.generateId(20));
											i.setReason(reason);
											i.setDate(new Date());
											i.setOrderId(order.getPublicId());
											i.setUserId(user.getEmail());
											i.setSuccess(false);
											i.setStatus("Pending");
											return i;
										});
							});
				}).map(i -> {
					i.setEvidence(images);
					return i;
				}).flatMap(refundRepo::save).map(i -> true).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());

	}

	@Override
	public Mono<Boolean> setNewBillingAndDeliveryAddress(Mono<BillingAndDeliveryAddressReq> req, String email) {
		return req.map(i -> modelMapper.map(i, BillingAndDeliveryAddressTable.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).flatMap(i -> {
					return userRepo.findByEmail(email)
							.switchIfEmpty(Mono.error(
									new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.map(user -> {
								int min = 10;
								long max = 100000000000000000L;
								Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);
								i.setId(random_int);
								i.setUserId(user.getEmail());
								user.setBillingAndDeliveryAddress(i.getId());
								userRepo.save(user).subscribe();
								return i;
							}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
				}).flatMap(billingAndDeliveryAddressRepo::save).map(i -> true).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.just(false));
	}

	@Override
	public Flux<BillingAndDeliveryAddressDto> getAllBillingAndDeliveryAddress(String user) {
		return billingAndDeliveryAddressRepo.findByUserId(user).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.map(i -> modelMapper.map(i, BillingAndDeliveryAddressDto.class));
	}

	@Override
	public Mono<Boolean> changeBillingAndDeliveryAddress(String user, Long billingId) {
		return billingAndDeliveryAddressRepo.findById(billingId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.map(billing -> userRepo.findByEmail(user).publishOn(Schedulers.boundedElastic())
						.subscribeOn(Schedulers.boundedElastic())
						.switchIfEmpty(Mono
								.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
						.map(i -> {
							i.setBillingAndDeliveryAddress(billing.getId());
							return i;
						}))
				.flatMap(__ -> __).map(userRepo::save).map(__ -> true).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.just(false));

	}

	@Override
	public void Send_OrderInformation_Email_And_PDF(Mono<OrderTable> order, String email, String orderId) {
		String html = order.map(i -> {
			Long billingAndDeliveryAddress = i.getBillingAndDeliveryAddress();
			String userName = i.getUserName();
			return userRepo.findByEmail(userName).map(user -> {
				return billingAndDeliveryAddressRepo.findById(billingAndDeliveryAddress).map(billing -> {
					return Flux.fromIterable(i.getFoodsInfo()).map(info -> {
						return foodRepo.findByPublicId(info.getFoodId()).map(d -> {
							List<FoodDto> foodDtos = new ArrayList<>();

							FoodInfoDto foodInfoDto = new FoodInfoDto();
							foodInfoDto.setCount(info.getCount());
							foodInfoDto.setPrice(info.getPrice());

							FoodDto foodDto = modelMapper.map(d,
									FoodDto.class);
							foodDto.setFoodInfo(foodInfoDto);
							foodDtos.add(foodDto);
							
							Map<String, Object> data = new HashMap<>();
							data.put("name", user.getEmail());
							data.put("deliveryAddress", billing.getDeliveryAdress());
							data.put("billingAddress", billing.getBillingAdress());
							try {
								data.put("imgUrl", ("http://" + InetAddress.getLocalHost().getHostAddress() + ":8081"
										+ d.getCoverImage()));
							} catch (Exception e) {
								data.put("order.imgUrl", d.getCoverImage());
							}
							data.put("orders", foodDtos);
							data.put("orderTotalPrice", i.getTotalPrice());
							Context thymeleafContext = new Context();
							thymeleafContext.setVariables(data);

							String htmlBody = thymeleafTemplateEngine.process("Order", thymeleafContext);
							return htmlBody;
						});
					}).flatMap(__ -> __);
				});
			}).flatMapMany(__ -> __);
		}).flatMapMany(__ -> __).distinct().blockLast().distinct().blockLast();

		try {
			byte[] asByteArray = utils.getAllBytesPdf(html, orderId);
			redisServiceImpl.savePdf(asByteArray, orderId);
		} catch (Exception e) {
		}
		sendMail(email, html);
	}

	private void sendMail(String email, String html) {
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
				.withRegion(Regions.AP_SOUTH_1).build();
		SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(email))
				.withMessage(
						new Message()
								.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(html))
										.withText(new Content().withCharset("UTF-8")
												.withData(html.replaceAll("\\<.*?\\>", ""))))
								.withSubject(new Content().withCharset("UTF-8").withData("Order Information")))
				.withSource(FROM);
		client.sendEmail(request);
	}

	@Override
	public Mono<Boolean> orderCompleted(String userId, String orderId) {
		return trackingDetailsRepo.findByOrderId(orderId)
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.doOnNext(i -> {
					i.setOrderDelivered(true);
					i.setDeliveryStatus("Delivered successfully");
				}).flatMap(trackingDetailsRepo::save).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(i -> {
					return userRepo.findByEmail(userId)
							.switchIfEmpty(Mono.error(
									new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.map(user -> {
								String token = "";
								try {
									SignedJWT createSignedJWT = tokenCreator.createSignedJWT(user.getEmail());
									token = tokenCreator.encryptToken(createSignedJWT);
								} catch (Exception e) {

								}
								Map<String, Object> data = new HashMap<>();
								String uri = ("http://localhost:8085/orders/order-confrim-web?token=" + token + "&user="
										+ userId + "&orderId=" + orderId);
								data.put("url", uri);
								String text = "Your" + " " + i.getOrderId() + "order is completed!";
								data.put("emailText", text);
								Context thymeleafContext = new Context();
								thymeleafContext.setVariables(data);
								String htmlBody = thymeleafTemplateEngine.process("ShopOrderCompleted",
										thymeleafContext);
								sendMail(user.getEmail(), htmlBody);
								return true;
							});
				}).switchIfEmpty(Mono.just(false));
	}

	@Override
	public Mono<Boolean> orderAccepted(String userId, String orderId) {
		return orderRepo.findByPublicId(orderId)
				.switchIfEmpty(
						Mono.error(new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.doOnNext(i -> {
					i.setOrderAccepted(true);
				}).flatMap(orderRepo::save).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(i -> {
					return userRepo.findByEmail(userId)
							.switchIfEmpty(Mono.error(
									new OrderSerivceNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.map(user -> {
								Map<String, Object> data = new HashMap<>();
								String text = "Your" + " " + i.getPublicId() + "order is Accepted!";
								data.put("order", text);
								Context thymeleafContext = new Context();
								String htmlBody = thymeleafTemplateEngine.process("OrderAccepted", thymeleafContext);
								sendMail(user.getEmail(), htmlBody);
								return true;
							});
				}).switchIfEmpty(Mono.just(false));
	}

	@Override
	public Flux<Boolean> saveManyOrderFromCart(Flux<CartDto> cart, String email) {

		return null;
	}

	@Override
	public Mono<Boolean> saveOrderFromCart(Mono<CartDto> cart, String email) {
		// TODO Auto-generated method stub
		return null;
	}
}