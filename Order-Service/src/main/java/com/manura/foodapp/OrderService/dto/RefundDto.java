package com.manura.foodapp.OrderService.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


import lombok.Data;

@Data
public class RefundDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4210045108215130712L;
	private String id;
    private String reason;
    private Date date;
    private List<String> evidence;
    private Boolean success;
    private OrderDto order;
	private String status;
}
