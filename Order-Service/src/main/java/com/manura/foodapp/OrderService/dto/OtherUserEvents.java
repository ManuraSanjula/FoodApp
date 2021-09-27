package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class OtherUserEvents implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3629312147298653236L;
	private String date;
	private String message;
	private String user;
}
