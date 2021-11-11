package com.manura.foodapp.dto;

import java.io.Serializable;
import java.util.Date;


public class UserDto implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 3583491748880254458L;
	private String email;
    private String pic;
    private Date passwordChangedAt;
    private Boolean active;
    private Boolean emailVerify;
    private Boolean accountNonLocked = true;
    private Boolean accountNonExpired = true;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public Date getPasswordChangedAt() {
		return passwordChangedAt;
	}
	public void setPasswordChangedAt(Date passwordChangedAt) {
		this.passwordChangedAt = passwordChangedAt;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Boolean getEmailVerify() {
		return emailVerify;
	}
	public void setEmailVerify(Boolean emailVerify) {
		this.emailVerify = emailVerify;
	}
	public Boolean getAccountNonLocked() {
		return accountNonLocked;
	}
	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}
	public Boolean getAccountNonExpired() {
		return accountNonExpired;
	}
	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}
}
