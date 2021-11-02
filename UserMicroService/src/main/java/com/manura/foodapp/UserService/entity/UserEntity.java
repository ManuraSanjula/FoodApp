package com.manura.foodapp.UserService.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserEntity implements Serializable, UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 6994849949494494L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Builder.Default
	private Boolean accountNonLocked = true;

	@Builder.Default
	private Boolean accountNonExpired = true;

	@Column(unique = true, nullable = false)
	private String publicId;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	private Boolean active;
	private Boolean emailVerify;

	@Column(nullable = false)
	private String address;

	private String emailVerificationToken;

	@Column(length = 2000)
	private String passwordResetToken;

	private Date passwordChangedAt;

	private String pic;

	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Collection<RoleEntity> role;

	@Override
	public void eraseCredentials() {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();

		Collection<AuthorityEntity> authorityEntities = new ArrayList<>();
		Collection<RoleEntity> roles  = this.getRole();

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
		return this.emailVerify;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.active;
	}
}
