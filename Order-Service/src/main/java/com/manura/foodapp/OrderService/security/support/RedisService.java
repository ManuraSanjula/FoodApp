package com.manura.foodapp.OrderService.security.support;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;

import reactor.core.publisher.Mono;

public interface RedisService {
   Mono<Document> getPdfAsDocument(String name);
   Mono<byte[]>  getPdfAsByteArray(String name);
   void savePdf(byte[] asByteArray,String name);
   Mono<Resource> getPdfAsResource(String name);
}
