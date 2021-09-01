/**
 * 
 */
package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.Data;

/**
 * @author manura-sanjula
 *
 */
@Table("BillingAndDeliveryAddress")
@Data
public class BillingAndDeliveryAddressTable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6889874480066945328L;
	@PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.DESCENDING)
	private Long id;
	private String billingAdress;
	private String deliveryAdress;
	private String userId;
}
