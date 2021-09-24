package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;


@Data
public class OrderDto implements Serializable {
	private static final long serialVersionUID = -3160948633567292934L;
	private String id;
	private List<FoodDto> food;	
	private Double totalPrice;
}
