package com.manura.foodapp.FoodService.entity;

import java.io.Serializable;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document
public class UserEntity implements Serializable,UserDetails, CredentialsContainer,Principal  {
    private static final long serialVersionUID = 89984844849448L;
    @Id
    private String id;
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
	@Override
	public void eraseCredentials() {		
	}
	@Override
	public String getPassword() {
		return null;
	}
	@Override
	public String getUsername() {
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
		return this.emailVerify;
	}

	@Override
	public boolean isEnabled() {
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
