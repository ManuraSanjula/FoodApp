package com.manura.foodapp.CartService.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
@Accessors(chain = true)
@Data
public class CartTable implements Serializable, Persistable<Integer> {

	private static final long serialVersionUID = 1162253883799444803L;
	@Id
	private Integer id;
	private String publicId;

	@Transient
	private FoodTable food;

	@Transient
	private UserTable user;

	@Transient
	private boolean CartTable;

	@CreatedDate
	private LocalDateTime createdDate;

	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	@Override
	@Transient
	public boolean isNew() {
		return this.CartTable || id == null;
	}

	public CartTable setAsNew() {
		this.CartTable = true;
		return this;
	}

	private Long count;

	private Double price;

}
