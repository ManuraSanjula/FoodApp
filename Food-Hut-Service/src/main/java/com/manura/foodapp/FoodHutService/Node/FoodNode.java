package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.neo4j.springframework.data.core.schema.GeneratedValue;
import org.neo4j.springframework.data.core.schema.Id;
import org.neo4j.springframework.data.core.schema.Node;

import lombok.Data;

@Data
@Node("Food")
public class FoodNode implements Serializable,Comparable<FoodNode>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9180316348117018580L;

	@Id
	@GeneratedValue
    String id;
	
    String name;
    String publicId;
    String description;
    String type;
    Integer unlikes;
    Integer likes;
    Double price;
    Double rating;
    Map<String, Integer> nutrition;
    String coverImage;
    
    List<String> images = new ArrayList<String>();
    Boolean offered = true;
   
	@Override
	public int compareTo(FoodNode o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;	}
}
