package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;


@Data
public class FullOrderDto implements Serializable {
	
	private static final long serialVersionUID = -2533811984825469740L;

	private UserDto user;
	private TrackingDetailsDto trackingDetails;
	private String id;
	private List<FoodDto> food;	
	private Double totalPrice;

}
