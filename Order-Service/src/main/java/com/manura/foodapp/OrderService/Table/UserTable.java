package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("User")
public class UserTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6049048397507165659L;
	@PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.DESCENDING)
	private Long id;
	@PrimaryKeyColumn(name = "publicId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String publicId;
	private String firstName;
	private String lastName;
	@PrimaryKeyColumn(name = "email", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String email;
	private Boolean active;
	private Boolean emailVerify;
	private String address;
	private String pic;
	private Long billingAndDeliveryAddress;
}
