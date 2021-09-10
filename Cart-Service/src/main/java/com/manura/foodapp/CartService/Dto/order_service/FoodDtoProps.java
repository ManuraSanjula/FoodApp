package com.manura.foodapp.CartService.Dto.order_service;

import java.io.Serializable;

import lombok.Data;

@Data
public class FoodDtoProps implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -8432342856750076026L;
	private String foodId;
	private Long count;
	private Double price;
}
