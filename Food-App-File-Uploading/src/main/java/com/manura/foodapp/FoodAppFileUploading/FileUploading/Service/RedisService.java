package com.manura.foodapp.FoodAppFileUploading.FileUploading.Service;

import org.springframework.core.io.Resource;

import com.manura.foodapp.FoodAppFileUploading.Redis.Model.ImageCachingRedis;

import reactor.core.publisher.Mono;

public interface RedisService {
	public void save(ImageCachingRedis obj);
	public Mono<Void> ifCacheEmpty(String name,byte [] array);
	Mono<Resource> getResource(String name);
}
