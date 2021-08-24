package com.manura.foodapp.CartService.Controller.Req.Model;

import java.io.Serializable;

import lombok.Data;

@Data
public class CartReq implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7966396970175121116L;
	private String food;
	private Long count;
}
