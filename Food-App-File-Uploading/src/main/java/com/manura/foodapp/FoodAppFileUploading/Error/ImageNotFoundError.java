package com.manura.foodapp.FoodAppFileUploading.Error;

public class ImageNotFoundError extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1923959438688743817L;

	public ImageNotFoundError(String message) {
        super(message);
    }
}
