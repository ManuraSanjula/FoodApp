package com.manura.foodapp.OrderService.Controller.Req;

import java.io.Serializable;

import lombok.Data;

@Data
public class BillingAndDeliveryAddressReq  implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4180425426327727954L;
	private String billingAdress;
	private String deliveryAdress;
}
