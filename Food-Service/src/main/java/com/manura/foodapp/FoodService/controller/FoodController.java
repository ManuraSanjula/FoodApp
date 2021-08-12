package com.manura.foodapp.FoodService.controller;

import java.security.Principal;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.manura.foodapp.FoodService.Error.Model.FoodError;
import com.manura.foodapp.FoodService.Error.Model.FoodNotFoundError;
import com.manura.foodapp.FoodService.controller.Model.Req.CommentReq;
import com.manura.foodapp.FoodService.controller.Model.Req.FoodReq;
import com.manura.foodapp.FoodService.controller.Model.Res.HalfFoodRes;
import com.manura.foodapp.FoodService.dto.CommentsDto;
import com.manura.foodapp.FoodService.dto.FoodDto;
import com.manura.foodapp.FoodService.service.impl.FoodServiceImpl;
import com.manura.foodapp.FoodService.util.ErrorMessages;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/foods")
public class FoodController {

	@Autowired
	FoodServiceImpl foodServiceImpl;

	ModelMapper modelMapper = new ModelMapper();

	@GetMapping
	Flux<HalfFoodRes> getAllFoods(@RequestParam(required = false) String type,
			@RequestParam(required = false) String name, @RequestParam(required = false) Boolean sort) {

		if (type != null)
			return foodServiceImpl.findByType(type).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class)).sort();

		else if (name != null)
			return foodServiceImpl.findByNames(name).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class)).sort();

		else if (name != null && type != null)
			return foodServiceImpl.findByTypeAndName(name, type).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class)).sort();

		else
			return foodServiceImpl.findAll().publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> modelMapper.map(i, HalfFoodRes.class)).sort();
	}

	@GetMapping("/{id}")
	Mono<FoodDto> getOneFood(@PathVariable String id) {
		return foodServiceImpl.findById(id).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())));
	}
	
	@PutMapping("/{id}/coverImage")
	Mono<ResponseEntity<FoodDto>> uploadCoverImage(@PathVariable String id,@RequestPart("coverImg") Mono<FilePart> fileParts) {
		return foodServiceImpl.uploadCoverImage(id,fileParts).publishOn(Schedulers.boundedElastic()).map(ResponseEntity::ok)
				.subscribeOn(Schedulers.boundedElastic()).defaultIfEmpty(ResponseEntity.notFound().build())
				.switchIfEmpty(Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())));
	}
	
	@PutMapping("/{id}/images")
	Mono<ResponseEntity<FoodDto>> uploadImages(@PathVariable String id,@RequestPart("images") Flux<FilePart> fileParts) {
		return foodServiceImpl.uploadImages(id,fileParts).publishOn(Schedulers.boundedElastic()).map(ResponseEntity::ok)
				.subscribeOn(Schedulers.boundedElastic()).defaultIfEmpty(ResponseEntity.notFound().build())
				.switchIfEmpty(Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())));
	}
	
	@GetMapping("/{id}/comments")
	Flux<CommentsDto> getAllComment(@PathVariable String id) {
		return foodServiceImpl.findAllComment(id).publishOn(Schedulers.boundedElastic())
				.publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new FoodNotFoundError(ErrorMessages.NO_RECORD_FOUND.getErrorMessage())));
	}

	@PostMapping
	Mono<ResponseEntity<FoodDto>> insertFood(@RequestBody Mono<FoodReq> foodReq) {
		return foodReq.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).flatMap(i -> {
			if (i.getDescription() == null || i.getFoodHutsIds() == null || i.getType() == null || i.getPrice() == null
					|| i.getType() == null) {
				return Mono.error(new FoodError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage()));
			}

			return foodServiceImpl.save(Mono.just(modelMapper.map(i, FoodDto.class)), i.getFoodHutsIds());
		}).map(ResponseEntity::ok).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.defaultIfEmpty(ResponseEntity.internalServerError().build());
	}


	@PutMapping("/{id}")
	Mono<ResponseEntity<FoodDto>> updateFood(@PathVariable String id, @RequestBody Mono<FoodReq> foodReq) {

		return foodReq.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).flatMap(i -> {
			return foodServiceImpl.update(id, Mono.just(modelMapper.map(i, FoodDto.class)), i.getFoodHutsIds());
		}).map(ResponseEntity::ok).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.defaultIfEmpty(ResponseEntity.internalServerError().build());
	}

	@PostMapping("/{id}/comments")
	Mono<ResponseEntity<CommentsDto>> addComment(@PathVariable String id,
			@RequestBody Mono<CommentReq> commentReq, Mono<Principal> principal) {
		return commentReq.flatMap(i -> {

			if (i.getDescription() == null) {
				return Mono.error(new FoodError(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage()));
			}
			
			return principal.map(Principal::getName).map(usr -> {
				return foodServiceImpl.saveComment(id, Mono.just(modelMapper.map(i, CommentsDto.class)), usr);
			}).flatMap(u->u);
		}).map(ResponseEntity::ok).publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic())
				.defaultIfEmpty(ResponseEntity.internalServerError().build());
	}
	
	  @GetMapping("/location-near")
	  public final Flux<FoodDto> getLocations(
	    @RequestParam("lat") Double latitude,
	    @RequestParam("long") Double longitude,
	    @RequestParam("d") double distance) {

	    return foodServiceImpl.findByLocationNear(new Point(longitude, latitude),
	      new Distance(distance, Metrics.KILOMETERS))
	    		.publishOn(Schedulers.boundedElastic())
				.subscribeOn(Schedulers.boundedElastic());
	  }
}