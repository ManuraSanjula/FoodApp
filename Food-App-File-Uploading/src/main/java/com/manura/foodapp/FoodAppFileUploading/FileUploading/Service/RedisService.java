package com.manura.foodapp.FoodAppFileUploading.FileUploading.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;

import com.manura.foodapp.FoodAppFileUploading.Redis.ImageCachingRedis;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RedisService {
	@Autowired
	ReactiveRedisTemplate<String, ImageCachingRedis> reactiveRedisTemplate;
	private ReactiveValueOperations<String, ImageCachingRedis> reactiveRedisTemplateOPs;

	@PostConstruct
	public void setup() {
		reactiveRedisTemplateOPs = reactiveRedisTemplate.opsForValue();

	}

	public void save(ImageCachingRedis obj) {
		try {
			reactiveRedisTemplateOPs.set(obj.getImageName(), obj).publishOn(Schedulers.boundedElastic())
			.subscribeOn(Schedulers.boundedElastic()).subscribe(i -> {
				
			});
		} catch (Exception e) {
			
		}
	}
	
	public Mono<Void> ifCacheEmpty(String name,byte [] array){
		ImageCachingRedis obj = new ImageCachingRedis();
		obj.setBytes(array);
		obj.setImageName(name);
		save(obj);
		return Mono.empty();
	}

	public Mono<Resource> getResource(String name) {
		try {
			return reactiveRedisTemplateOPs.get(name).switchIfEmpty(Mono.empty())
					.publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).map(i -> {
				InputStream inputStream = new ByteArrayInputStream(i.getBytes());
				InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
				return inputStreamResource;
			});
		} catch (Exception e) {
			return Mono.empty();
		}
	}

}
