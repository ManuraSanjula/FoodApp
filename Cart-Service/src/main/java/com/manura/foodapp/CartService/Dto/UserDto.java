package com.manura.foodapp.CartService.Dto;

import java.io.Serializable;


import lombok.Data;

@Data
public class UserDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4859269471989392962L;
	/**
	 * 
	 */
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String pic;
}
