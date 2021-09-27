//package com.manura.foodapp.NotificationMessages.WebSocketController;
//
//import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.annotation.SendToUser;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebSocketController {
//
//	@MessageMapping("/message")
//	@SendTo("/topic/reply")
//	public String processMessageFromClient(){
//		return "Hello ";
//	}
//	
//	@MessageExceptionHandler
//    @SendToUser("/topic/errors")
//    public String handleException(Throwable exception) {
//        return exception.getMessage();
//    }
//
//}