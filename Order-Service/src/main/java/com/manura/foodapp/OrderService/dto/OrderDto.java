package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;

import lombok.Data;


@Data
public class OrderDto implements Serializable {
	private static final long serialVersionUID = -3160948633567292934L;
	private String id;
	private FoodDto food;	
	private Long count;
	private Double price;

}
