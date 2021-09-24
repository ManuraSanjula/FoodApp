package com.manura.foodapp.UserService.UserServiceEvent;

import java.util.Date;

import lombok.Data;

@Data
public class OtherUserEvents {
	private Date date;
	private String message;
	private String user;
}
