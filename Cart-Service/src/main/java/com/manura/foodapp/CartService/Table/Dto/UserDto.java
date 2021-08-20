package com.manura.foodapp.CartService.Table.Dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
	
	private Long id;
	private String publicId;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean active;
	private Boolean emailVerify;
	private String address;
	private Date passwordChangedAt;
	private List<String> roles = new ArrayList<>();
	private List<String> authorities = new ArrayList<>();
	private String pic;
}
