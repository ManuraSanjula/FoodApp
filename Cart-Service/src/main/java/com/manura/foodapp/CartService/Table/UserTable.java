package com.manura.foodapp.CartService.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("User")
@Data
public class UserTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8848844735978074589L;
	@Id
	private Long id;
	private String publicId;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean active;
	private Boolean emailVerify;
	private String address;
	private Date passwordChangedAt;
	private List<String> roles = new ArrayList<>();
	private List<String> authorities = new ArrayList<>();
	private String pic;
}
