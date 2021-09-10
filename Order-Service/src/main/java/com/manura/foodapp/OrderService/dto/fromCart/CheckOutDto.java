package com.manura.foodapp.OrderService.dto.fromCart;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CheckOutDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6955311363540652216L;
	/**
	 * 
	 */
	String user;
	List<FoodDtoProps> cartDtos;
}
