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
import lombok.ToString;
import lombok.experimental.Accessors;

@Table("food")
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
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
	
	@CreatedDate
	private LocalDateTime createdDate;

	@LastModifiedDate
	private LocalDateTime lastModifiedDate;
	
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
