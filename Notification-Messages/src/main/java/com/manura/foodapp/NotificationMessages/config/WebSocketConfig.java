package com.manura.foodapp.NotificationMessages.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.Map;
import com.manura.foodapp.NotificationMessages.Service.NotificationService;

@Configuration
public class WebSocketConfig {
  @Autowired	
  private NotificationService notificationService;
  @Bean
  public HandlerMapping handlerMapping(){
      Map<String, WebSocketHandler> map = Map.of(
              "/notification", notificationService
      );
      return new SimpleUrlHandlerMapping(map, -1);
  }
  
}
