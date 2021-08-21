package com.manura.foodapp.CartService.Table.Dto;

import java.io.Serializable;
import java.util.Date;


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
	
    private Integer id;
	private String publicId;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean active;
	private Boolean emailVerify;
	private String address;
	private Date passwordChangedAt;
	private String pic;
}
