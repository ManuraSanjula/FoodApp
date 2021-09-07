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
import com.manura.foodapp.OrderService.Table.RefundTable;

import com.manura.foodapp.OrderService.Utils.ErrorMessages;
import com.manura.foodapp.OrderService.Utils.TokenCreator;
import com.manura.foodapp.OrderService.Utils.Utils;
import com.manura.foodapp.OrderService.controller.Req.BillingAndDeliveryAddressReq;
import com.manura.foodapp.OrderService.controller.Req.OrderReq;
import com.manura.foodapp.OrderService.dto.BillingAndDeliveryAddressDto;
import com.manura.foodapp.OrderService.dto.FoodDto;
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
													.switchIfEmpty(Mono.error(new OrderSerivceNotFoundError(
															ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
													.mapNotNull(billing -> {
														OrderTable orderTable = new OrderTable();
														orderTable.setAddress(user.getAddress());
														orderTable.setPublicId(utils.generateAddressId(30));
														orderTable.setUserName(user.getEmail());
														orderTable.setFood(food.getPublicId());
														orderTable.setCount(i.getCount());
														orderTable.setPrice((food.getPrice() * i.getCount()));
														orderTable.setStatus("processing");
														orderTable.setTrackingNumber(utils.generateAddressId(20));
														orderTable.setBillingAndDeliveryAddress(billing.getId());
														return orderTable;
													});
										}).flatMap(__ -> __).doOnNext(o -> {
											int min = 10;
											long max = 100000000000000000L;
											Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);
											o.setId(random_int);
										}).flatMap(orderRepo::save).doOnNext(d -> {
											TrackingDetailsTable trackingDetailsTable = new TrackingDetailsTable();
											trackingDetailsTable.setUserId(email);
											trackingDetailsTable.setOrderId(d.getPublicId());
											trackingDetailsTable.setDeliveryStatus("Not Delivered");

											int min = 10;
											long max = 100000000000000000L;
											Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);

											trackingDetailsTable.setId(random_int);
											trackingDetailsRepo.save(trackingDetailsTable).subscribe();
											Runnable orderInformation = () -> Send_OrderInformation_Email_And_PDF_Single(
													Mono.just(d), d.getUserName(), d.getPublicId());
											new Thread(orderInformation).start();
										}).mapNotNull(__ -> true);
							}).flatMap(__ -> __);
				}).flatMap(__ -> __).switchIfEmpty(Mono.just(false));
	}

	@Override
	public Flux<OrderDto> getOrder(String id) {
		return orderRepo.findByUserName(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(e -> {
					return foodRepo.findByPublicId(e.getFood()).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(i -> {
								OrderDto cart = new OrderDto();
								cart.setId(e.getPublicId());
								cart.setFood(modelMapper.map(i, FoodDto.class));
								cart.setCount(e.getCount());
								cart.setPrice(e.getPrice());
								return cart;
							}).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
				}).flatMap(u -> u).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
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
				.subscribeOn(Schedulers.boundedElastic()).mapNotNull(order -> {
					return trackingDetailsRepo.findByOrderId(order.getPublicId()).publishOn(Schedulers.boundedElastic())
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

					int min = 10;
					long max = 100000000000000000L;
					Long random_int = (long) Math.floor(Math.random() * (max - min + 1) + min);

					i.setId(random_int);
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
	public void Send_OrderInformation_Email_And_PDF_Single(Mono<OrderTable> order, String email, String orderId) {
		order.map(i -> {
			Long billingAndDeliveryAddress = i.getBillingAndDeliveryAddress();
			String foodId = i.getFood();
			String userName = i.getUserName();

			return userRepo.findByEmail(userName).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(user -> {
						return foodRepo.findByPublicId(foodId).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).map(food -> {
									return billingAndDeliveryAddressRepo.findById(billingAndDeliveryAddress)
											.publishOn(Schedulers.boundedElastic())
											.subscribeOn(Schedulers.boundedElastic()).map(billing -> {
												Map<String, Object> data = new HashMap<>();

												data.put("name", user.getEmail());
												data.put("deliveryAddress", billing.getDeliveryAdress());
												data.put("billingAddress", billing.getBillingAdress());

												try {
													data.put("imgUrl",
															("http://" + InetAddress.getLocalHost().getHostAddress()
																	+ ":8081" + food.getCoverImage()));
												} catch (Exception e) {
													data.put("order.imgUrl", food.getCoverImage());
												}
												data.put("foodName", food.getName());
												data.put("foodCount", i.getCount());
												data.put("foodPrice", food.getPrice());
												data.put("orderTotalPrice", i.getPrice());

												Context thymeleafContext = new Context();
												thymeleafContext.setVariables(data);

												String htmlBody = thymeleafTemplateEngine.process("SingleOrder",
														thymeleafContext);
												return htmlBody;
											});
								}).flatMap(__ -> __);
					}).flatMap(__ -> __);
		}).flatMap(__ -> __).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.subscribe(html -> {
					try {
						byte[] asByteArray = utils.getAllBytesPdf(html, orderId);
						redisServiceImpl.savePdf(asByteArray, orderId);
					} catch (Exception e) {
					}
					sendMail(email, html);
				});
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
	public void Send_OrderInformation_Email_And_PDF_Many(Mono<OrderTable> order, String email, String orderId) {

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
}