package com.manura.foodapp.Ui.Error.Exception;

public class UserServiceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1348771109171495607L;

    public UserServiceNotFoundException(String message) {
        super(message);
    }
}
