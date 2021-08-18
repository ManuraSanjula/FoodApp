package com.manura.foodapp.FoodHutService.RouterFunctions;

import java.util.Date;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.manura.foodapp.FoodHutService.Controller.Req.CommentReq;
import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutCreationReq;
import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutUpdateReq;
import com.manura.foodapp.FoodHutService.Controller.Res.FoodHutHalfRes;
import com.manura.foodapp.FoodHutService.Error.Model.FoodHutError;
import com.manura.foodapp.FoodHutService.Error.Model.Res.ErrorMessage;
import com.manura.foodapp.FoodHutService.Service.Impl.FoodHutServiceImpl;
import com.manura.foodapp.FoodHutService.dto.CommentsDto;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;
import com.manura.foodapp.FoodHutService.utils.ErrorMessages;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RequestHandler {

	@Autowired
	private FoodHutServiceImpl foodHutServiceImpl;
	private ModelMapper modelMapper = new ModelMapper();

	public Mono<ServerResponse> saveFooHut(ServerRequest serverRequest) {
		return serverRequest.bodyToMono(FoodHutCreationReq.class).publishOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.subscribeOn(Schedulers.boundedElastic()).map(i -> {
					FoodHutDto foodHutDto = modelMapper.map(i, FoodHutDto.class);
					return ServerResponse.ok().body(foodHutServiceImpl.save(Mono.just(foodHutDto), i.getFoodIds(),
							i.getLatitude(), i.getLongitude()), FoodHutDto.class);
				}).flatMap(i -> i);
	}

	public Mono<ServerResponse> getAllFoodHuts(ServerRequest serverRequest) {
		return ServerResponse.ok().body(foodHutServiceImpl.getAll(), FoodHutHalfRes.class)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<ServerResponse> getAllComments(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return ServerResponse.ok().body(foodHutServiceImpl.getAllComments(id), CommentsDto.class)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	public Mono<ServerResponse> getOneFoodHut(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return foodHutServiceImpl.getOne(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()))).map(i -> {
					return ServerResponse.ok().body(Mono.just(i), FoodHutDto.class)
							.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i -> i);

	}

	public Mono<ServerResponse> deleteComment(ServerRequest serverRequest) {
		String foodHutId = serverRequest.pathVariable("foodHutId");
		String commentId = serverRequest.pathVariable("commentId");
		return ServerResponse.ok().body(foodHutServiceImpl.deleteComment(foodHutId, commentId), Void.class)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());

	}

	public Mono<ServerResponse> setImages(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return foodHutServiceImpl
				.uploadImages(id,
						serverRequest.multipartData().map(it -> it.get("images")).flatMapMany(Flux::fromIterable)
								.cast(FilePart.class))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.mapNotNull(i -> ServerResponse.ok().body(Mono.just(i), FoodHutDto.class)
						.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()))
				.flatMap(i -> i);
	}

	public Mono<ServerResponse> setCoverImage(ServerRequest serverRequest) {

		String id = serverRequest.pathVariable("id");
		return serverRequest.body(BodyExtractors.toMultipartData()).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic()).flatMap(parts -> {
					Map<String, Part> singleValueMap = parts.toSingleValueMap();
					FilePart file = (FilePart) singleValueMap.get("coverImg");
					return foodHutServiceImpl.uploadCoverImage(id, Mono.just(file))
							.mapNotNull(i -> ServerResponse.ok().body(Mono.just(i), FoodHutDto.class)
									.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()))
							.flatMap(i -> i);
				});
	}

	public Mono<ServerResponse> updateFoodHut(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");

		return serverRequest.bodyToMono(FoodHutUpdateReq.class).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(req -> {
					return foodHutServiceImpl.update(id, Mono.just(req)).publishOn(Schedulers.boundedElastic())
							.subscribeOn(Schedulers.boundedElastic()).mapNotNull(i -> {
								return ServerResponse.ok().body(Mono.just(i), FoodHutDto.class)
										.switchIfEmpty(Mono.error(new FoodHutError(
												ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage())))
										.publishOn(Schedulers.boundedElastic())
										.subscribeOn(Schedulers.boundedElastic());
							});
				}).flatMap(i -> i).flatMap(i -> i).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());

	}

	public Mono<ServerResponse> updateComment(ServerRequest serverRequest) {
		String foodHutId = serverRequest.pathVariable("foodHutId");
		String commentId = serverRequest.pathVariable("commentId");
		return foodHutServiceImpl.updateComment(foodHutId, commentId, serverRequest.queryParam("comment").get())
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).map(i -> {
					return ServerResponse.ok().body(Mono.just(i), CommentsDto.class)
							.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i -> i);
	}

	public Mono<ServerResponse> saveComment(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return serverRequest.bodyToMono(CommentReq.class)
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage())))
				.mapNotNull(req -> {
					if (req.getRating() == null || req.getUserId() == null || req.getComment() == null) {
						ErrorMessage errorMessage = new ErrorMessage();
						errorMessage.setTimestamp(new Date());
						errorMessage.setMessage(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
						return ServerResponse.badRequest().body(Mono.just(errorMessage), ErrorMessage.class)
								.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
					}
					return ServerResponse.ok()
							.body(foodHutServiceImpl.addComment(id, Mono.just(req)), CommentsDto.class)
							.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
				}).flatMap(i -> i).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
}