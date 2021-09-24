package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class BillingAndDeliveryAddressDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5631792250228454535L;
	private Long id;
	private String billingAdress;
	private String deliveryAdress;
}
