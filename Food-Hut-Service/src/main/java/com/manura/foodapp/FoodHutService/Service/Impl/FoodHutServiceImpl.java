package com.manura.foodapp.FoodHutService.Service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.rsocket.RSocketRequester;
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
import com.manura.foodapp.FoodHutService.Redis.model.CommentCachingRedis;
import com.manura.foodapp.FoodHutService.Controller.Req.CommentReq;
import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutUpdateReq;
import com.manura.foodapp.FoodHutService.Controller.Res.FoodHutHalfRes;
import com.manura.foodapp.FoodHutService.Error.Model.FoodHutError;
import com.manura.foodapp.FoodHutService.Service.FoodHutService;
import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;
import com.manura.foodapp.FoodHutService.messaging.Pub;
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
	@Autowired
	private Pub pub;
	@Autowired
	private RedisServiceImpl redisServiceImpl;
	@Autowired
	private Mono<RSocketRequester> rSocketRequester;

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
					foodHut.setFoods(foods);
					foodHut.setComment(new HashSet<>());
					foodHut.setImageCover("/food-hut/FoodHutCoverImage");
					foodHut.setRatingsQuantity(4);
					foodHut.setImages(new ArrayList<>());
					return foodHut;
				}).flatMap(foodHutRepo::save).doOnNext(i -> {
					Runnable runnable = () -> {
						pub.pubFood(Mono.just(i), "created");
					};
					new Thread(runnable).start();
				}).mapNotNull(i -> modelMapper.map(i, FoodHutDto.class)).doOnNext(i -> {
					redisServiceImpl.save(i);
				});
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
									foods.addAll(i.getFoods());
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
							.subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
								Runnable runnable = () -> {
									pub.pubFood(Mono.just(i), "update");
								};
								new Thread(runnable).start();
							}).mapNotNull(i -> modelMapper.map(i, FoodHutDto.class));
				}).flatMap(i -> i);
	}

	@Override
	public Flux<FoodHutHalfRes> getAll() {
		return foodHutRepo.findAll().map(i -> modelMapper.map(i, FoodHutHalfRes.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	private Mono<FoodHutDto> foodHutNoCache(String id) {
		return foodHutRepo.findByPublicId(id).mapNotNull(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
					redisServiceImpl.save(i);
				});
	}

	@Override
	public Mono<FoodHutDto> getOne(String id) {
		return redisServiceImpl.getOneFoodHut(id).switchIfEmpty(foodHutNoCache(id))
				.mapNotNull(i -> modelMapper.map(i, FoodHutDto.class)).publishOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.subscribeOn(Schedulers.boundedElastic());
	}

	@Override
	public Mono<CommentsDto> addComment(String id, Mono<CommentReq> comment) {
		return comment.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(i -> {
					return foodHutRepo.findByPublicId(id)
							.switchIfEmpty(
									Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.mapNotNull(foodHut -> {
								Set<FoodHutHasComment> commentNodes = new HashSet<>();
								commentNodes.addAll(foodHut.getComment());
								return userRepo.findByPublicId(i.getUserId()).publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic())
										.switchIfEmpty(Mono.error(
												new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
										.mapNotNull(user -> {
											CommentNode commentNode = new CommentNode();
											commentNode.setUser(user);
											commentNode.setRating(i.getRating());
											commentNode.setComment(i.getComment());
											commentNode.setUserImage(user.getPic());
											commentNode.setCreatedAt(new Date());
											commentNode.setPublicId(utils.generateId(80));
											return commentNode;
										}).flatMap(commentRepo::save).mapNotNull(j -> {
											FoodHutHasComment foodHutHasComment = new FoodHutHasComment();
											foodHutHasComment.setComment(j);
											commentNodes.add(foodHutHasComment);
											foodHut.setComment(commentNodes);
											foodHutRepo.save(foodHut).subscribe();
											return j;
										}).mapNotNull(j -> modelMapper.map(j, CommentsDto.class))
										.publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic());

							}).flatMap(j -> j).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i -> i).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).doOnNext(i->{
					redisServiceImpl.addNewComment(id, i).subscribe();
				});
	}

	@Override
	public Mono<UserNode> addUser(Mono<UserNode> user) {
		return user.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.flatMap(userRepo::save).doOnNext(redisServiceImpl::addNewUser);
	}

	@Override
	public Mono<UserNode> updateUser(String id, Mono<UserNode> user) {
		return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(addUser(user)).mapNotNull(usr -> {
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
				}).flatMap(i -> i).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.doOnNext(i->{
					redisServiceImpl.updateUser(id, i);
				});
	}

	@Override
	public Mono<FoodNode> addFood(Mono<FoodNode> food) {
		return food.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.flatMap(foodRepo::save);
	}

	@Override
	public Mono<FoodNode> updateFood(String id, Mono<FoodNode> food) {
		return foodRepo.findByPublicId(id).switchIfEmpty(addFood(food)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).mapNotNull(i -> {
					return food.mapNotNull(req -> {
						req.setId(i.getId());
						req.setPublicId(i.getPublicId());
						return req;
					}).flatMap(foodRepo::save);
				}).flatMap(u -> u);
	}

	@Override
	public Mono<CommentsDto> updateComment(String foodHutId, String commentId, String comment) {
		return commentRepo.findByPublicId(commentId)
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.doOnNext(i -> i.setComment(comment)).flatMap(commentRepo::save)
				.map(i -> modelMapper.map(i, CommentsDto.class)).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.doOnNext(i -> {
					redisServiceImpl.commentUpdated(foodHutId, i.getId(),
							modelMapper.map(i, CommentsDto.class)).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
				});

	}

	@Override
	public Mono<Void> deleteComment(String foodHutId, String commentId) {
		return foodHutRepo.findByPublicId(foodHutId).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.mapNotNull(foodHut -> {
					return commentRepo.findByPublicId(commentId)
							.publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic())
							.switchIfEmpty(
									Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
							.mapNotNull(i -> {
								Set<FoodHutHasComment> comments = new HashSet<>();
								comments.addAll(foodHut.getComment());
								comments.removeIf(j -> j.getComment().getPublicId().equals(i.getPublicId()));
								foodHut.setComment(comments);
								foodHutRepo.save(foodHut).subscribe();
								redisServiceImpl.deleteComment(foodHut.getPublicId(), i.getPublicId()).subscribe();
								return commentRepo.deleteByPublicId(commentId);
							}).flatMap(i -> i).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i -> i);
	}

	private Flux<CommentsDto> commentNoCache(String id) {
		return foodHutRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.empty())
				.flatMapMany(i -> {
					List<FoodHutHasComment> commsts = new ArrayList<>();
					commsts.addAll(i.getComment());
					
					List<CommentsDto> commentsDtos = new ArrayList<>();
					commsts.forEach(c->{
						commentsDtos.add(modelMapper.map(c.getComment(), CommentsDto.class));
					});
					CommentCachingRedis commentCachingRedis = new CommentCachingRedis();
					commentCachingRedis.setName("Comment" + id);
					commentCachingRedis.setComment(commentsDtos);
					redisServiceImpl.saveComment(("Comment" + id), commentCachingRedis);
					
					return Flux.fromIterable(commsts);
				}).map(i -> modelMapper.map(i.getComment(), CommentsDto.class));
	}

	@Override
	public Flux<CommentsDto> getAllComments(String id) {
		return redisServiceImpl.getAllComments(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(commentNoCache(id));
	}

	@Override
	public Mono<FoodHutDto> uploadCoverImage(String id, Mono<FilePart> filePartFlux) {
		return foodHutRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.mapNotNull(i -> {
					String name = filePartFlux.publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).map(part -> {

								return this.rSocketRequester.publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic())
										.map(rsocket -> rsocket.route("file.upload.foodHut").data(part.content()))
										.map(r -> r.retrieveFlux(String.class)).flatMapMany(s -> s);
							}).flatMapMany(s -> s).blockLast();

					if (name != null) {
						String image = ("/foodHut-image/" + name);
						i.setImageCover(image);
					} else {
						throw new FoodHutError("image processing failed");
					}
					return i;
				}).flatMap(foodHutRepo::save).mapNotNull(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.doOnNext(i -> {
					redisServiceImpl.save(i);
				});
	}

	@Override
	public Mono<FoodHutDto> uploadImages(String id, Flux<FilePart> filePartFlux) {
		return foodHutRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.mapNotNull(food -> {
					List<String> urls = new ArrayList<String>();

					return filePartFlux.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
							.switchIfEmpty(Mono
									.error(new FoodHutError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
							.mapNotNull(file -> {

								String image = this.rSocketRequester
										.map(rsocket -> rsocket.route("file.upload.foodHut").data(file.content()))
										.mapNotNull(r -> r.retrieveFlux(String.class)).flatMapMany(s -> s).distinct()
										.publishOn(Schedulers.boundedElastic()).blockFirst();

								if (image != null) {
									String fullImageUril = ("/foodHut-image/" + image);
									urls.add(fullImageUril);
								}
								return urls;
							}).mapNotNull(i -> {
								food.setImages(i);
								return food;
							}).blockLast();
				}).flatMap(foodHutRepo::save).mapNotNull(i -> modelMapper.map(i, FoodHutDto.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
					redisServiceImpl.save(i);
				});
	}
	
	private Mono<UserNode> ifUserAbsentInCache(String id) {
		return userRepo.findByPublicId(id).publishOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.empty())
				.subscribeOn(Schedulers.boundedElastic()).doOnNext(i -> {
					redisServiceImpl.addNewUser(i).subscribe();
				});
	}

	@Override
	public Mono<UserNode> getUser(String user) {
		return redisServiceImpl.getUser(user).publishOn(Schedulers.boundedElastic()).switchIfEmpty(ifUserAbsentInCache(user))
				.subscribeOn(Schedulers.boundedElastic());
	}
}
