package com.manura.foodapp.CartService.Table;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("Cart")
@Data
public class CartTable implements Serializable {
	
	private static final long serialVersionUID = 1162253883799444803L;
	@Id
	private Long id;
	private String publicId;

	private FoodTable food;
	
	private UserTable user;
	
	private Long count;
}
