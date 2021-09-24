package com.manura.foodapp.FoodHutService.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class UserDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6831069731408356681L;
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
