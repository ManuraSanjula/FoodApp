package com.manura.foodapp.FoodAppFileUploading.FileUploading.Controller;

import java.io.IOException;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.manura.foodapp.FoodAppFileUploading.FileUploading.Service.FileStorageService;
import com.manura.foodapp.FoodAppFileUploading.Utils.Utils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class RSocketController {
	
   @Autowired	
   private FileStorageService fileStorageService;
   
   @Autowired
   private Utils utils;
   
   @MessageMapping("file.upload.user")
   public Flux<String> userUpload(@Payload Flux<DataBuffer> content) throws IOException {
	   String fileName = ("User" + utils.generateName(30)+".jpeg");
	   var path = Paths.get(fileName+".jpeg");
	   return Flux.concat(fileStorageService.uploadFileUser(path, content,fileName), Mono.just(fileName));
   }
   
   @MessageMapping("file.upload.food")
   public Flux<String> foodUploadCoverImage(@Payload Flux<DataBuffer> content) throws IOException {
	   String fileName = ("Food" + utils.generateName(30));
	   var path = Paths.get(fileName+".jpeg");
	   return Flux.concat(fileStorageService.uploadFileFood(path, content,fileName), Mono.just(fileName));
   }
}
