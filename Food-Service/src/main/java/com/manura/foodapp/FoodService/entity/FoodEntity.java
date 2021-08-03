package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document
@Data
public class FoodEntity implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3765005311194667388L;
	@Id
    String id;
    String name;
    String publicId;
    String description;
    String type;
    
    
    Integer unlikes = 0;
    
    Integer likes = 0;
    
    Integer price = 5;
    
    Integer rating = 3;
    
    Map<String, Integer> nutrition;
    
    String coverImage = "FoodCoverImage";
    
    List<String> images = new ArrayList<String>();
    
    Boolean offered = true;
    @DBRef
    List<CommentsEntity> comments = new ArrayList<CommentsEntity>();
    
    @DBRef
    List<FoodHutEntity> foodHuts =  new ArrayList<FoodHutEntity>();
}
