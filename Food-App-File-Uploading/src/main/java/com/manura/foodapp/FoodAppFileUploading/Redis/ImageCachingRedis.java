package com.manura.foodapp.FoodAppFileUploading.Redis;

import java.io.Serializable;

import lombok.Data;

@Data
public class ImageCachingRedis implements Serializable {/**
	 * 
	 */
	private static final long serialVersionUID = 860328650843339704L;
	private String imageName;
    private byte [] bytes;
}
