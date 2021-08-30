package com.manura.foodapp.FoodAppFileUploading.FileUploading.Service;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileStorageService {
	public Flux<String> uploadFileUser(Path path, Flux<DataBuffer> bufferFlux, String fileName);
	public Flux<String> uploadFileFood(Path path, Flux<DataBuffer> bufferFlux, String fileName);
	public Flux<String> uploadFileFoodHut(Path path, Flux<DataBuffer> bufferFlux, String fileName);
	public Flux<String> uploadFileRefund(Path path, Flux<DataBuffer> bufferFlux, String fileName);
	public Mono<Resource> loadFileAsResourceIfCacheNotPresent(String fileName,String type);
	public Mono<Resource> loadFileAsResource(String fileName, String type);
}
