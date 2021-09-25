package com.manura.foodapp.NotificationMessages.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class OtherUserEvents implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4131573915573354530L;
	private String date;
	private String message;
	private String user;
}