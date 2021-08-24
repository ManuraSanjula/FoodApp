package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Document
@EqualsAndHashCode
public class FoodHutEntity implements Serializable, Comparable<FoodHutEntity> {
	private static final long serialVersionUID = 4303115012939347345L;
	@Id
	private String id;
	private String name;
	private String address;
	private Boolean open;
	private List<String> phoneNumbers;
	private String image;
	@DBRef
	private Set<FoodEntity> foods = new HashSet<FoodEntity>();
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
	private GeoJsonPoint location;

	@Override
	public int compareTo(FoodHutEntity o) {
		// TODO Auto-generated method stub
		return this.id.equals(o.getId()) ? 1 : 0;
	}

}
