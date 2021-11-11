package com.manura.foodapp;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.inject.Singleton;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

@Startup
@Singleton
public class RabbitMq {
	@Lock(LockType.READ)
	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public void saveUser() {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare("user_created_foodapp_review", false, false, false, null);
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
			};
			channel.basicConsume("user_created_foodapp_review", true, deliverCallback, consumerTag -> {
			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
