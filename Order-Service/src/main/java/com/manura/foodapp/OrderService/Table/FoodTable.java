package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("Food")
public class FoodTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7507702972023215096L;
	@PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Integer id;
	@PrimaryKeyColumn(name = "name", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String name;
	@PrimaryKeyColumn(name = "publicId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String publicId;
	private String description;
	private String type;
	private Integer unlikes;
	private Integer likes;
	private Double price;
	private Double rating;
	private String coverImage;

}
