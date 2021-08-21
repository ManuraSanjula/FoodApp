package com.manura.foodapp.CartService.Table;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table
@Data
public class FoodTable implements Serializable,Persistable<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5270310274724521373L;
	
	@Id
    private Integer id;
	private String name;
	private String publicId;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private String coverImage;
	private Boolean offered = true;
	
	@Transient
    private boolean newFoodTable;
	
	@Override
    @Transient
    public boolean isNew() {
        return this.newFoodTable || id == null;
    }

    public FoodTable setAsNew(){
        this.newFoodTable = true;
        return this;
    }
}
