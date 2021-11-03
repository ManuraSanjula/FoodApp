package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("User")
@EqualsAndHashCode
public class UserNode implements Serializable{
	private static final long serialVersionUID = -3034787297243469089L;
	@Id
	@GeneratedValue
	private Long id;
	private String publicId;
	private String firstName;
	private String lastName;
	private String email;
	private Boolean active;
	private Boolean emailVerify;
	private String address;
	private Date passwordChangedAt;
	@Builder.Default
	private List<String> roles = new ArrayList<>();
	@Builder.Default
	private List<String> authorities = new ArrayList<>();
	@Builder.Default
	private Boolean accountNonLocked = true;
	@Builder.Default
	private Boolean accountNonExpired = true;
	private String pic;
	private Timestamp createdDate;
    private Timestamp lastModifiedDate;
}