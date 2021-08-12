package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Document
@EqualsAndHashCode
public class FoodHutEntity implements Serializable,Comparable<FoodHutEntity> {
	private static final long serialVersionUID = 4303115012939347345L;
    @Id
    String id;
    String name;
    String address;
    Boolean open;
    List<String> phoneNumbers;
    String image;
    @DBRef
    List<FoodEntity> foods =  new ArrayList<FoodEntity>();
    private GeoJsonPoint location;
	@Override
	public int compareTo(FoodHutEntity o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 :0;	}

}
