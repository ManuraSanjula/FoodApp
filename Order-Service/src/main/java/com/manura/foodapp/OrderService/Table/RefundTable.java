package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

@Data
@Table("Refund")
public class RefundTable implements Serializable {
	private static final long serialVersionUID = -6103550661489428597L;
	@PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.DESCENDING)
	private Long id;
	
	@PrimaryKeyColumn(name = "publicId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String publicId;
    private String reason;
    private Date date;
    private String orderId;
    private String userId;
    private List<String> evidence;
    private Boolean success;
	private String status;
    private String feedBackFromCompany;
}
