package com.manura.foodapp.FoodHutService.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Node("User")
@EqualsAndHashCode
public class UserNode implements Serializable, Comparable<UserNode> {
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
	private List<String> roles = new ArrayList<>();
	private List<String> authorities = new ArrayList<>();
	private String pic;
	@Override
	public int compareTo(UserNode o) {
		return this.id.equals(o.getId()) ? 1 :0;
	}
}