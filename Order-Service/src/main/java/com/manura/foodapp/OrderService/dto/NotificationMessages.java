package com.manura.foodapp.OrderService.dto;

import java.util.Date;


import lombok.Data;

@Data
public class NotificationMessages {
	private Date date;
	private String message;
	private String user;
}
