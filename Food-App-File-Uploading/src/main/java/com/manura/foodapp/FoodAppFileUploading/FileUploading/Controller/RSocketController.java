package com.manura.foodapp.FoodAppFileUploading.FileUploading.Controller;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.manura.foodapp.FoodAppFileUploading.FileUploading.Service.Impl.FileStorageServiceImpl;
import com.manura.foodapp.FoodAppFileUploading.Utils.Utils;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Controller
public class RSocketController {

	@Autowired
	private FileStorageServiceImpl fileStorageService;

	@Autowired
	private Utils utils;

	@MessageMapping("file.upload.user")
	public Flux<String> userUpload(@Payload Flux<DataBuffer> content) {
		String fileName = ("User" + utils.generateName(30));
		var path = Paths.get(fileName + ".jpeg");
		return fileStorageService.uploadFileUser(path, content, fileName)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}

	@MessageMapping("file.upload.food")
	public Flux<String> foodUploadCoverImage(@Payload Flux<DataBuffer> content)  {
		String fileName = ("Food" + utils.generateName(30));
		var path = Paths.get(fileName + ".jpeg");
		return fileStorageService.uploadFileFood(path, content, fileName)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
	
	@MessageMapping("file.upload.foodHut")
	public Flux<String> foodHutUploadCoverImage(@Payload Flux<DataBuffer> content)  {
		String fileName = ("FoodHut" + utils.generateName(30));
		var path = Paths.get(fileName + ".jpeg");
		return fileStorageService.uploadFileFoodHut(path, content, fileName)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
	
	@MessageMapping("file.upload.refund")
	public Flux<String> refundUploadCoverImage(@Payload Flux<DataBuffer> content)  {
		String fileName = ("Refund" + utils.generateName(30));
		var path = Paths.get(fileName + ".jpeg");
		return fileStorageService.uploadFileRefund(path, content, fileName)
				.publishOn(Schedulers.boundedElastic()).subscribeOn(Schedulers.boundedElastic());
	}
}
