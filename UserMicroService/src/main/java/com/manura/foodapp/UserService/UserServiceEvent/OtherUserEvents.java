package com.manura.foodapp.UserService.UserServiceEvent;

import java.io.Serializable;

import lombok.Data;

@Data
public class OtherUserEvents implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -822537013255326018L;
	private String date;
	private String message;
	private String user;
}
