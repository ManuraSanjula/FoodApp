package com.manura.foodapp.OrderService.Table;

import java.io.Serializable;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("User")
public class UserTable implements Serializable, UserDetails, CredentialsContainer,Principal {
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
	@Builder.Default
	private List<String> roles = new ArrayList<>();
	@Builder.Default
	private List<String> authorities = new ArrayList<>();
	@Builder.Default
	private Boolean accountNonLocked = true;
	@Builder.Default
	private Boolean accountNonExpired = true;
	private Timestamp createdDate;
	private Timestamp lastModifiedDate;

	@Override
	public void eraseCredentials() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return this.emailVerify;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.active;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> user_authorities = new ArrayList<>();
        roles.forEach(role->{
        	user_authorities.add(new SimpleGrantedAuthority(role));
        });
        authorities.forEach(auth->{
        	user_authorities.add(new SimpleGrantedAuthority(auth));
        });
		return user_authorities;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.email;
	}
}