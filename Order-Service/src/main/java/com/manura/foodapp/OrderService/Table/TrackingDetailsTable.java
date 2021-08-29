package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Table("TrackingDetails")
@Data
public class TrackingDetailsTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8065973735562598805L;
	@PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private Long id;
	private Long orderId;
	private Long userId;
	private String deliveryStatus;
}
