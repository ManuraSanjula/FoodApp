package com.manura.foodapp.FoodHutService.RouterFunctions;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.manura.foodapp.FoodHutService.Controller.Req.CommentReq;
import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutCreationReq;
import com.manura.foodapp.FoodHutService.Controller.Req.FoodHutUpdateReq;
import com.manura.foodapp.FoodHutService.Controller.Res.FoodHutHalfRes;
import com.manura.foodapp.FoodHutService.Error.Model.FoodHutError;
import com.manura.foodapp.FoodHutService.Service.Impl.FoodHutServiceImpl;
import com.manura.foodapp.FoodHutService.dto.FoodHutDto;
import com.manura.foodapp.FoodHutService.utils.ErrorMessages;

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
	public Mono<ServerResponse> getOneFoodHut(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return ServerResponse.ok().body(foodHutServiceImpl.getOne(id), FoodHutDto.class)
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
	public Mono<ServerResponse> updateFoodHut(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return ServerResponse.ok().body(foodHutServiceImpl.update(id,serverRequest.bodyToMono(FoodHutUpdateReq.class)), FoodHutDto.class)
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage())))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
	
	public Mono<ServerResponse> saveComment(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		return ServerResponse.ok().body(foodHutServiceImpl.addComment(id,serverRequest.bodyToMono(CommentReq.class)), FoodHutDto.class)
				.switchIfEmpty(Mono.error(new FoodHutError(ErrorMessages.COULD_NOT_CREATE_RECORD.getErrorMessage())))
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
}