package com.manura.foodapp.NotificationMessages.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manura.foodapp.NotificationMessages.Controller.dto.NotificationMessages;

import reactor.core.publisher.Flux;

@RestController
public class NotificationMessagesController {
	
	@Autowired
	private Flux<NotificationMessages> notificationBroadcast;
	
	@RequestMapping(value="{user}/notification",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<NotificationMessages> getAllNotificationBroadcasts(@PathVariable String user){
		return notificationBroadcast.filter(i->i.getUser().equals(user));
	}

}
