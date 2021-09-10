package com.manura.foodapp.OrderService.dto.fromCart;

import java.io.Serializable;

import lombok.Data;

@Data
public class FoodDtoProps implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 6492587817611524506L;
	/**
		 * 
		 */
	private String foodId;
	private Long count;
	private Double price;
}
