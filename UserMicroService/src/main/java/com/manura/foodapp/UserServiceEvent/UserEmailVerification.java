package com.manura.foodapp.UserServiceEvent;

import com.manura.foodapp.shared.AmazonSES;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEmailVerification implements Runnable {

    private String email;
    private AmazonSES amazonSES;
    private String token;

    private void event() {
        try {
            amazonSES.verifyEmail(email, token);
        } catch (Exception e) {
            amazonSES.verifyEmail(email, token);
        }
    }

    @Override
    public void run() {
        event();
    }

}
