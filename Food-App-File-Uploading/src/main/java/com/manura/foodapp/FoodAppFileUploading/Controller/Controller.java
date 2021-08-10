package com.manura.foodapp.FoodAppFileUploading.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.manura.foodapp.FoodAppFileUploading.Error.ImageNotFoundError;
import com.manura.foodapp.FoodAppFileUploading.FileUploading.Service.FileStorageService;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@org.springframework.stereotype.Controller
public class Controller {

	@Autowired
	FileStorageService fileStorageService;

	@GetMapping(value = "/user-image/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Mono<ResponseEntity<Resource>> getUserImage(@PathVariable String fileName) {
		Resource fileAsResource = fileStorageService.loadFileAsResourceUser(fileName);
		if (fileAsResource == null) {
			return Mono.error(new ImageNotFoundError(""));
		} else {
			return Mono.just(fileAsResource).publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic()).switchIfEmpty(Mono.error(new ImageNotFoundError("")))
					.map(ResponseEntity::ok);
		}

	}
	
	@GetMapping(value = "/food-image/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Mono<ResponseEntity<Resource>> getFoodImage(@PathVariable String fileName) {
		Resource fileAsResource = fileStorageService.loadFileAsResourceFood((fileName+".jpeg"));
		if (fileAsResource == null) {
			return Mono.error(new ImageNotFoundError(""));
		} else {
			return Mono.just(fileAsResource)
					.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
					.switchIfEmpty(Mono.error(new ImageNotFoundError("")))
					.map(ResponseEntity::ok);
		}

	}

	@RequestMapping("/**")
	Mono<String> notFound() {
		return Mono.just("notFound")
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
}

@ControllerAdvice
class ErrorController {
	@ExceptionHandler(ImageNotFoundError.class)
	Mono<String> notFound(ImageNotFoundError ex) {
		return Mono.just("notFound")
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
}
