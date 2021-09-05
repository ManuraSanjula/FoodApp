package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("Orders")
public class OrderTable implements Serializable {
	
	private static final long serialVersionUID = 1628047444012394138L;
	
	@PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.DESCENDING)
	private Long id;
	
	@PrimaryKeyColumn(name = "publicId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String publicId;
	
	@PrimaryKeyColumn(name = "userName", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String userName;
	
	@PrimaryKeyColumn(name = "food", ordinal = 3, type = PrimaryKeyType.PARTITIONED)
	private String food;

	private Long count;

	private Double price;
	
	private String address;
	
	private String status;
	private String trackingNumber;
	private Long billingAndDeliveryAddress;
	private Boolean orderAccepted;
	private Boolean orderRecive;
}

