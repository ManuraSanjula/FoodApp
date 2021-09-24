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
import com.manura.foodapp.FoodAppFileUploading.FileUploading.Service.Impl.FileStorageServiceImpl;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@org.springframework.stereotype.Controller
public class Controller {

	@Autowired
	FileStorageServiceImpl fileStorageService;

	@GetMapping(value = "/user-image/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Mono<ResponseEntity<Resource>> getUserImage(@PathVariable String fileName) {
		return fileStorageService.loadFileAsResource(fileName+".jpeg","User").
			publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
			.switchIfEmpty(Mono.error(new ImageNotFoundError("")))
			.map(ResponseEntity::ok);
	}
	
	@GetMapping(value = "/food-image/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Mono<ResponseEntity<Resource>> getFoodImage(@PathVariable String fileName) {		
		return fileStorageService.loadFileAsResource(fileName+".jpeg","Food").
				publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new ImageNotFoundError("")))
				.map(ResponseEntity::ok);

	}
	
	@GetMapping(value = "/foodHut-image/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Mono<ResponseEntity<Resource>> getFoodHutImage(@PathVariable String fileName) {		
		return fileStorageService.loadFileAsResource(fileName+".jpeg","FoodHut").
				publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new ImageNotFoundError("")))
				.map(ResponseEntity::ok);

	}
	
	@GetMapping(value = "/refund-image/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Mono<ResponseEntity<Resource>> getRefundImage(@PathVariable String fileName) {		
		return fileStorageService.loadFileAsResource(fileName+".jpeg","Refund").
				publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic())
				.switchIfEmpty(Mono.error(new ImageNotFoundError("")))
				.map(ResponseEntity::ok);

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
