package com.manura.foodapp.FoodHutService.Service.Impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodHutService.FoodHutServiceApplication.CommentRepo;
import com.manura.foodapp.FoodHutService.FoodHutServiceApplication.FoodHutRepo;
import com.manura.foodapp.FoodHutService.FoodHutServiceApplication.FoodRepo;
import com.manura.foodapp.FoodHutService.FoodHutServiceApplication.UserRepo;
import com.manura.foodapp.FoodHutService.Node.CommentNode;
import com.manura.foodapp.FoodHutService.Node.FoodHutNode;
import com.manura.foodapp.FoodHutService.Node.FoodNode;
import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.Node.Relationship.FoodHutHasComment;
import com.manura.foodapp.FoodHutService.Node.Relationship.FoodHutHasFood;
import com.manura.foodapp.FoodHutService.Controller.Req.CommentReq;
import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutUpdateReq;
import com.manura.foodapp.FoodHutService.Controller.Res.FoodHutHalfRes;
import com.manura.foodapp.FoodHutService.Error.Model.FoodHutError;
import com.manura.foodapp.FoodHutService.Service.FoodHutService;
import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;
import com.manura.foodapp.FoodHutService.utils.ErrorMessages;
import com.manura.foodapp.FoodHutService.utils.Utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class FoodHutServiceImpl implements FoodHutService {

	@Autowired
	private FoodRepo foodRepo;
	@Autowired
	private FoodHutRepo foodHutRepo;
	@Autowired
	private CommentRepo commentRepo;

	@Autowired
	private UserRepo userRepo;

	private ModelMapper modelMapper = new ModelMapper();
	@Autowired
	private Utils utils;

	@Override
	public Mono<FoodHutDto> save(Mono<FoodHutDto> dto, List<String> foodIds, Double latitude, Double longitude) {

		return dto.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.COULD_NOT_CREATE_RECORD.getErrorMessage())))
				.mapNotNull(i -> modelMapper.map(i, FoodHutNode.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).mapNotNull(foodHut -> {
					Point location = new Point(longitude, latitude);
					Set<FoodHutHasFood> foods = new HashSet<>();
					foodIds.forEach(i -> {
						foodRepo.findByPublicId(i).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).subscribe(food -> {
									if (food != null) {
										FoodHutHasFood foodHutHasFood = new FoodHutHasFood();
										foodHutHasFood.setFood(food);
										foods.add(foodHutHasFood);
									}
								});
					});
					foodHut.setLocation(location);
					foodHut.setPublicId(utils.generateId(80));
					foodHut.setFood(foods);
					foodHut.setComment(new HashSet<>());
					return foodHut;
				}).flatMap(foodHutRepo::save).mapNotNull(i -> modelMapper.map(i, FoodHutDto.class));
	}

	@Override
	public Mono<FoodHutDto> update(String id, Mono<FoodHutUpdateReq> dto) {
		return dto.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage())))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.mapNotNull(updateReq -> {
					return foodHutRepo.findByPublicId(id).switchIfEmpty(Mono.empty())
							.switchIfEmpty(
									Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
							.mapNotNull(i -> {
								if (updateReq.getName() != null) {
									i.setName(updateReq.getName());
								}
								if (updateReq.getGroupSizePerTable() != null) {
									i.setGroupSizePerTable(updateReq.getGroupSizePerTable());
								}
								if (updateReq.getRatingsQuantity() != null) {
									i.setRatingsQuantity(updateReq.getRatingsQuantity());
								}
								if (updateReq.getSummary() != null) {
									i.setSummary(updateReq.getSummary());
								}
								if (updateReq.getDescription() != null) {
									i.setDescription(updateReq.getDescription());
								}
								if (updateReq.getOpentAt() != null) {
									i.setOpentAt(updateReq.getOpentAt());
								}
								if (updateReq.getFoodIds() != null) {
									Set<FoodHutHasFood> foods = new HashSet<>();
									foods.addAll(i.getFood());
									updateReq.getFoodIds().forEach(j -> {
										foodRepo.findByPublicId(j).publishOn(Schedulers.boundedElastic())
												.subscribeOn(Schedulers.boundedElastic()).subscribe(food -> {
													if (food != null) {
														FoodHutHasFood foodHutHasFood = new FoodHutHasFood();
														foodHutHasFood.setFood(food);
														foods.add(foodHutHasFood);
													}
												});
									});
								}
								if (updateReq.getLatitude() != null && updateReq.getLongitude() != null) {
									Point location = new Point(updateReq.getLongitude(), updateReq.getLatitude());
									i.setLocation(location);
								}
								return i;
							}).flatMap(foodHutRepo::save).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic())
							.mapNotNull(i -> modelMapper.map(i, FoodHutDto.class));
				}).flatMap(i -> i);
	}

	@Override
	public Flux<FoodHutHalfRes> getAll() {
		return foodHutRepo.findAll().map(i -> modelMapper.map(i, FoodHutHalfRes.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodHutDto> getOne(String id) {

		return foodHutRepo.findByPublicId(id).switchIfEmpty(Mono.empty())
				.mapNotNull(i -> modelMapper.map(i, FoodHutDto.class)).publishOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<CommentsDto> addComment(String id, Mono<CommentReq> comment) {
		return comment
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(i -> {
					return foodHutRepo.findByPublicId(id).switchIfEmpty(Mono.empty())
							.switchIfEmpty(
									Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.mapNotNull(foodHut -> {
								Set<FoodHutHasComment> commentNodes = new HashSet<>();
								commentNodes.addAll(foodHut.getComment());
								return userRepo.findByPublicId(i.getUserId()).switchIfEmpty(Mono.empty())
										.switchIfEmpty(Mono.error(
												new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
										.mapNotNull(user -> {
											CommentNode commentNode = new CommentNode();
											commentNode.setFoodHut(foodHut);
											commentNode.setUser(user);
											commentNode.setRating(i.getRating());
											commentNode.setComment(i.getComment());
											commentNode.setUserImage(user.getPic());
											commentNode.setCreatedAt(new Date());
											commentNode.setPublicId(utils.generateId(80));
											return commentNode;
										}).flatMap(commentRepo::save).map(j -> {
											FoodHutHasComment foodHutHasComment = new FoodHutHasComment();
											foodHutHasComment.setComment(j);
											commentNodes.add(foodHutHasComment);
											foodHut.setComment(commentNodes);
											foodHutRepo.save(foodHut).subscribe();
											return j;
										}).mapNotNull(j -> modelMapper.map(j, CommentsDto.class));

							}).flatMap(j -> j);
				}).flatMap(i -> i);
	}

	@Override
	public Mono<UserNode> addUser(Mono<UserNode> user) {
		return user.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.flatMap(userRepo::save);
	}

	@Override
	public Mono<UserNode> updateUser(String id, Mono<UserNode> user) {
		return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(addUser(user)).mapNotNull(usr -> {
					return user.publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
							.subscribeOn(Schedulers.boundedElastic())
							.mapNotNull(i -> {
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
							}).flatMap(userRepo::save);
				}).flatMap(i -> i);
	}

	@Override
	public Mono<FoodNode> addFood(Mono<FoodNode> food) {
		return food.doOnNext(i -> i.setId(null)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(foodRepo::save);
	}

	@Override
	public Mono<FoodNode> updateFood(String id, Mono<FoodNode> food) {
		return foodRepo.findByPublicId(id).switchIfEmpty(Mono.empty()).switchIfEmpty(addFood(food))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(i -> {
					return food.map(req -> {
						req.setId(i.getId());
						req.setPublicId(i.getPublicId());
						return req;
					}).flatMap(foodRepo::save);
				}).flatMap(u -> u);
	}
}