package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;

import lombok.Data;


@Data
public class FoodInfoDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -853674095272609306L;
	private Double price;
    private Long count;
}
