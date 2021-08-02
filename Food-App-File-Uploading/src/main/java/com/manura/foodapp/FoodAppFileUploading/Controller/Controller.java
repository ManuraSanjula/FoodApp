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

@org.springframework.stereotype.Controller
public class Controller {

	@Autowired
	FileStorageService fileStorageService;

	@GetMapping(value = "/user-image/{fileName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
	public Mono<ResponseEntity<Resource>> getImage(@PathVariable String fileName) {
		Resource fileAsResource = fileStorageService.loadFileAsResource(fileName);
		if (fileAsResource == null) {
			return Mono.error(new ImageNotFoundError(""));
		} else {
			return Mono.just(fileAsResource).switchIfEmpty(Mono.error(new ImageNotFoundError("")))
					.map(ResponseEntity::ok);
		}

	}

	@RequestMapping("/**")
	Mono<String> notFound() {
		return Mono.just("notFound");
	}
}

@ControllerAdvice
class ErrorController {
	@ExceptionHandler(ImageNotFoundError.class)
	Mono<String> notFound(ImageNotFoundError ex) {
		return Mono.just("notFound");
	}
}
