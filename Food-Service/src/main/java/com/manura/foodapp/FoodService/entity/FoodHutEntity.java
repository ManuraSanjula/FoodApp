package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.manura.foodapp.FoodService.anotation.CascadeSave;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Document
@EqualsAndHashCode
public class FoodHutEntity implements Serializable {

	private static final long serialVersionUID = 4303115012939347345L;
	
    @Id
    String id;
    String name;
    String address;
    Boolean open;
    List<String> phoneNumbers;
   
    String image;
    
    @DBRef(lazy = true)
    @CascadeSave
    List<FoodEntity> foods =  new ArrayList<FoodEntity>();
}
