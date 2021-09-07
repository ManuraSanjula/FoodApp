package com.manura.foodapp.OrderService.dto;

import lombok.Data;

@Data
public class CartDto {
	private Integer id;
	private String publicId;
	private String userName;
	private String food;
	private Integer userId;
	private Integer foodId;
}
