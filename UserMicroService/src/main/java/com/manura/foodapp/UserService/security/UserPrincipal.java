package com.manura.foodapp.UserService.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.manura.foodapp.UserService.entity.AuthorityEntity;
import com.manura.foodapp.UserService.entity.RoleEntity;
import com.manura.foodapp.UserService.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UserPrincipal implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	UserEntity userEntity;

	public UserPrincipal(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		List<GrantedAuthority> authorities = new ArrayList<>();

		Collection<AuthorityEntity> authorityEntities = new ArrayList<>();
		Collection<RoleEntity> roles  = userEntity.getRole();

		if(roles == null){
			return  authorities;
		}
		roles.forEach(role->{
			authorities.add(new SimpleGrantedAuthority(role.getRole()));
			authorityEntities.addAll(role.getAuthorities());
		});
		authorityEntities.forEach(authority->{
			authorities.add(new SimpleGrantedAuthority(authority.getName()));
		});
		return  authorities;
	}

	@Override
	public String getPassword() {
		return userEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return userEntity.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return userEntity.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return userEntity.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return userEntity.getActive();
	}

}
