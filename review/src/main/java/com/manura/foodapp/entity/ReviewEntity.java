package com.manura.foodapp.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "review")
public class ReviewEntity implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7291083068201425825L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String comment;
	@ManyToOne
	private UserEntity user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public UserEntity getUserEntity() {
		return user;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.user = userEntity;
	}

}
