/**
 * 
 */
package com.manura.foodapp.OrderService.Service.Impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import com.manura.foodapp.OrderService.Redis.Model.PdfRedis;
import com.manura.foodapp.OrderService.security.support.RedisService;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * @author manura-sanjula
 *
 */
@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
	ReactiveRedisTemplate<String, PdfRedis> reactiveRedisTemplate;
	private ReactiveValueOperations<String, PdfRedis> reactiveRedisTemplateOps;
	
	private Document obtenerDocumentDeByte(byte[] documentoXml){
	    try {
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setNamespaceAware(true);
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    return builder.parse(new ByteArrayInputStream(documentoXml));
	    }catch (Exception e) {
	    	return null;
		}
	}

	@PostConstruct
	public void setup() {
		reactiveRedisTemplateOps = reactiveRedisTemplate.opsForValue();
	}

	@Override
	public Mono<Document> getPdfAsDocument(String name) {
		try {
			return reactiveRedisTemplateOps.get(name).map(i->obtenerDocumentDeByte(i.getBytes()))
					.publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic())
					.switchIfEmpty(Mono.empty())
					;
		}catch (Exception e) {
			return Mono.empty();
		}
	}

	@Override
	public void savePdf(byte[] asByteArray, String name) {
		PdfRedis pdfRedis = new PdfRedis();
		pdfRedis.setBytes(asByteArray);
		pdfRedis.setFileName(name);
		try {
			reactiveRedisTemplateOps.set(pdfRedis.getFileName(), pdfRedis).publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic()).subscribe(i->{
						System.out.println(i);
					});
		} catch (Exception e) {
			
		}
	}
	
	public Mono<Resource> getPdfAsResource(String name) {
		try {
			return reactiveRedisTemplateOps.get(name).switchIfEmpty(Mono.empty())
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
	
	
	@Override
	public Mono<byte[]> getPdfAsByteArray(String name) {
		try {
			return reactiveRedisTemplateOps.get(name).map(i->i.getBytes())
					.publishOn(Schedulers.boundedElastic())
					.subscribeOn(Schedulers.boundedElastic());
		}catch (Exception e) {
			return Mono.empty();
		}
	}
}
