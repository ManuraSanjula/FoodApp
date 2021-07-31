package com.manura.foodapp.Event;

import com.manura.foodapp.shared.AmazonSES;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserPasswordReset implements Runnable {

    private String email;
    private AmazonSES amazonSES;
    private String token;
    private String firstName;

    private void event() {
        amazonSES.sendPasswordResetRequest(firstName, email, token);
    }

    @Override
    public void run() {
        event();
    }

}
