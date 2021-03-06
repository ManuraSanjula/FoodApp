package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Node("Food")
@EqualsAndHashCode
public class FoodNode implements Serializable {
	
	private static final long serialVersionUID = 9180316348117018580L;

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String publicId;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private String coverImage;

	private List<String> images = new ArrayList<String>();
	private Boolean offered = true;
}