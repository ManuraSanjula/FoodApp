package com.manura.foodapp.OrderService.Redis.Model;

import java.io.Serializable;

import lombok.Data;

@Data
public class PdfRedis implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 860328650843339704L;
	private String fileName;
    private byte [] bytes;
}
