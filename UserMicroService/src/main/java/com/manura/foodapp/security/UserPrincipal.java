package com.manura.foodapp.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.manura.foodapp.entity.AuthorityEntity;
import com.manura.foodapp.entity.RoleEntity;
import com.manura.foodapp.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UserPrincipal implements UserDetails {

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
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}