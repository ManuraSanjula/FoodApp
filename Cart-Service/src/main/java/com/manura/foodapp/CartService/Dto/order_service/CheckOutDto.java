package com.manura.foodapp.CartService.Dto.order_service;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CheckOutDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7018454105049374329L;
	String user;
	List<FoodDtoProps> cartDtos;
}
