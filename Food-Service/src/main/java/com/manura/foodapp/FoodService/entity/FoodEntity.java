package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document
@Data
public class FoodEntity implements Serializable,Comparable<FoodEntity>{
	private static final long serialVersionUID = -3765005311194667388L;
	@Id
	private  String id;
	@Indexed(unique = true)
	private String name;
	private String publicId;
	private String description;
	private  String type;
	private  Integer unlikes;
	private  Integer likes;
	private Double price;
	private Double rating;
	private Map<String, Integer> nutrition;
	private String coverImage;
	private List<String> images = new ArrayList<String>();
	private Boolean offered = true;
    @DBRef(lazy = true)
    private List<CommentsEntity> comments;
    @DBRef(lazy = true)
    private List<FoodHutEntity> foodHuts;
	@Override
	public int compareTo(FoodEntity o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;	}
}
