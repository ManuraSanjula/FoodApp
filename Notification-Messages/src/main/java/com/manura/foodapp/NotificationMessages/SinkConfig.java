package com.manura.foodapp.NotificationMessages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.manura.foodapp.NotificationMessages.Controller.dto.NotificationMessages;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Configuration
public class SinkConfig {
  @Bean
  public Sinks.Many<NotificationMessages> sink(){
	  return Sinks.many().replay().limit(1);
  }
  
  @Bean
  public Flux<NotificationMessages> notificationBroadcast(Sinks.Many<NotificationMessages> sink){
	  return sink.asFlux();
  }
}
