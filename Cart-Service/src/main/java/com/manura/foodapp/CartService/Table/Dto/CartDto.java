package com.manura.foodapp.CartService.Table.Dto;

import java.io.Serializable;

import lombok.Data;


@Data
public class CartDto implements Serializable {
	private static final long serialVersionUID = -3160948633567292934L;
	private FoodDto food;
	
	private UserDto user;
	
	private Long count;
}
