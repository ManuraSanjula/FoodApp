package com.manura.foodapp.OrderService.controller.Req;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderReq  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7678825723625967743L;
	private String food;
	private Long count;
}
