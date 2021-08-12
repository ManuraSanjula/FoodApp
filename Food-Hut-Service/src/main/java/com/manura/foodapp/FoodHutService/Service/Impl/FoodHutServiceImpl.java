package com.manura.foodapp.FoodHutService.Service.Impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutUpdateReq;
import com.manura.foodapp.FoodHutService.Controller.Res.FoodHutHalfRes;
import com.manura.foodapp.FoodHutService.Error.Model.FoodHutError;
import com.manura.foodapp.FoodHutService.Node.FoodHutNode;
import com.manura.foodapp.FoodHutService.Node.FoodNode;
import com.manura.foodapp.FoodHutService.Node.UserNode;
import com.manura.foodapp.FoodHutService.Repo.CommentRepo;
import com.manura.foodapp.FoodHutService.Repo.FoodHutRepo;
import com.manura.foodapp.FoodHutService.Repo.FoodRepo;
import com.manura.foodapp.FoodHutService.Repo.UserRepo;
import com.manura.foodapp.FoodHutService.Service.FoodHutService;
import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;
import com.manura.foodapp.FoodHutService.dto.UserDto;
import com.manura.foodapp.FoodHutService.utils.ErrorMessages;

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

	@Override
	public Mono<FoodHutDto> save(Mono<FoodHutDto> dto, List<String> foodIds, Double latitude, Double longitude) {

		return dto.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(i -> modelMapper.map(i, FoodHutNode.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).map(foodHut -> {
					Point location = new Point(longitude, latitude);
					Set<FoodNode> foods = new HashSet<>();
					foodIds.forEach(i -> {
						foodRepo.findByPublicId(i).publishOn(Schedulers.boundedElastic())
								.subscribeOn(Schedulers.boundedElastic()).subscribe(food -> {
									if (food != null) {
										foods.add(food);
									}
								});
					});
					foodHut.setLocation(location);
					foodHut.setFood(foods);
					return foodHut;
				}).flatMap(foodHutRepo::save).map(i -> modelMapper.map(i, FoodHutDto.class));
	}

	@Override
	public Mono<FoodHutDto> update(String id, Mono<FoodHutUpdateReq> dto) {
		return dto.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage())))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.mapNotNull(updateReq -> {
					return foodHutRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty()).map(i -> {
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
									Set<FoodNode> foods = new HashSet<>();
									foods.addAll(i.getFood());
									updateReq.getFoodIds().forEach(j -> {
										foodRepo.findByPublicId(j).publishOn(Schedulers.boundedElastic())
												.subscribeOn(Schedulers.boundedElastic()).subscribe(food -> {
													if (food != null) {
														foods.add(food);
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
							.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, FoodHutDto.class));
				}).flatMap(i -> i);
	}

	@Override
	public Flux<FoodHutHalfRes> getAll() {
		return foodHutRepo.findAll().map(i -> modelMapper.map(i, FoodHutHalfRes.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<FoodHutDto> getOne(String id) {

		return foodHutRepo.findByPublicId(id).switchIfEmpty(Mono.empty()).map(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<CommentsDto> addComment(Mono<CommentsDto> comment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<UserDto> addUser(Mono<UserNode> user) {
		return user.map(i -> modelMapper.map(i, UserNode.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(userRepo::save)
				.map(i -> modelMapper.map(i, UserDto.class));
	}

	@Override
	public Mono<UserDto> updateUser(String id, Mono<UserNode> user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<FoodDto> addFood(Mono<FoodNode> food) {
		return food.map(i -> modelMapper.map(i, FoodNode.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(foodRepo::save)
				.map(i -> modelMapper.map(i, FoodDto.class));
	}

	@Override
	public Mono<FoodDto> updateFood(String id, Mono<FoodNode> food) {
		// TODO Auto-generated method stub
		return null;
	}

}
