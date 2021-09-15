package com.manura.foodapp.NotificationMessages.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manura.foodapp.NotificationMessages.Controller.dto.NotificationMessages;

import reactor.core.publisher.Sinks;

@Service
public class Sub {

	@Autowired
	private ObjectMapper objectMapper;
	
	private Sinks.Many<NotificationMessages> sink;

	@RabbitListener(queues = "user_verify_email", concurrency = "20")
	public void user_verify_email(String message) {
		try {
			NotificationMessages notificationMessages = objectMapper.readValue(message, NotificationMessages.class);
			sink.tryEmitNext(notificationMessages);
		} catch (Exception e) {
		}
	}

	@RabbitListener(queues = "user_password_reset_success", concurrency = "20")
	public void user_password_success(String message) {
		try {
			NotificationMessages notificationMessages = objectMapper.readValue(message, NotificationMessages.class);
			sink.tryEmitNext(notificationMessages);
		} catch (Exception e) {
		}
	}

	@RabbitListener(queues = "user_security", concurrency = "20")
	public void user_security(String message) {
		try {
			NotificationMessages notificationMessages = objectMapper.readValue(message, NotificationMessages.class);
			sink.tryEmitNext(notificationMessages);
		} catch (Exception e) {
		}
	}

	@RabbitListener(queues = "order_confirm", concurrency = "20")
	public void order_confirm(String message) {
		try {
			NotificationMessages notificationMessages = objectMapper.readValue(message, NotificationMessages.class);
			sink.tryEmitNext(notificationMessages);
		} catch (Exception e) {
		}
	}

	@RabbitListener(queues = "order_completed", concurrency = "20")
	public void order_completed(String message) {
		try {
			NotificationMessages notificationMessages = objectMapper.readValue(message, NotificationMessages.class);
			sink.tryEmitNext(notificationMessages);
		} catch (Exception e) {
		}
	}

	@RabbitListener(queues = "order_accepted", concurrency = "20")
	public void order_accepted(String message) {
		try {
			NotificationMessages notificationMessages = objectMapper.readValue(message, NotificationMessages.class);
			sink.tryEmitNext(notificationMessages);
		} catch (Exception e) {
		}
	}

	@RabbitListener(queues = "order_refund_done", concurrency = "20")
	public void order_refund_done(String message) {
		try {
			NotificationMessages notificationMessages = objectMapper.readValue(message, NotificationMessages.class);
			sink.tryEmitNext(notificationMessages);
		} catch (Exception e) {
		}
	}
}
