package com.manura.foodapp.CartService.Table;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table
@Data
public class CartTable implements Serializable ,Persistable<Integer> {
	
	private static final long serialVersionUID = 1162253883799444803L;
	@Id
    private Integer id;
	private String publicId;

	private FoodTable food;
	
	private UserTable user;
	
	@Transient
    private boolean CartTable;
	
	@Override
    @Transient
    public boolean isNew() {
        return this.CartTable || id == null;
    }

    public CartTable setAsNew(){
        this.CartTable = true;
        return this;
    }
	
	private Long count;

	
}
