package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;

import lombok.Data;


@Data
public class FullOrderDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2533811984825469740L;
	private String id;
	private FoodDto food;
	private UserDto user;
	private Long count;
	private Double price;
	private TrackingDetailsDto trackingDetails;

}
