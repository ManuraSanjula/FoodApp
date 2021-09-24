package com.manura.foodapp.CartService.Dto;

import java.io.Serializable;

import lombok.Data;


@Data
public class CartDto implements Serializable {
	private static final long serialVersionUID = -3160948633567292934L;
	private String id;
	private FoodDto food;
	
	private UserDto user;
	
	private Long count;
	
	private Double price;

}
