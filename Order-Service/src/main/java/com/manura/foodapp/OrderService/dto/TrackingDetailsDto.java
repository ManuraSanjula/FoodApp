package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;


import lombok.Data;

@Data
public class TrackingDetailsDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -147368638818598334L;
	/**
	 * 
	 */
	private Long id;
	private String orderId;
	private String userId;
	private String deliveryStatus;
	private Boolean orderDelivered;
}
