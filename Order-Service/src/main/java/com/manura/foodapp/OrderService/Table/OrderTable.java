package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;

import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;


@Data
@Table("Order")
public class OrderTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1628047444012394138L;
	
	@PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Integer id;
	
	@PrimaryKeyColumn(name = "publicId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String publicId;
	
	@PrimaryKeyColumn(name = "userName", ordinal = 0, type = PrimaryKeyType.PARTITIONED)

	private String userName;
	
	@PrimaryKeyColumn(name = "food", ordinal = 0, type = PrimaryKeyType.PARTITIONED)

	private String food;

	@Transient
	private FoodTable item;

	@Transient
	private UserTable owner;

	private Integer userId;
	
	private Integer foodId;
	
	private Long count;

	private Double price;
}
