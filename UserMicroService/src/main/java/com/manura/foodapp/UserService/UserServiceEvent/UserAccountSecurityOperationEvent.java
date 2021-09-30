package com.manura.foodapp.UserService.UserServiceEvent;

import java.net.InetAddress;

import com.manura.foodapp.UserService.shared.AmazonSES;

public class UserAccountSecurityOperationEvent {
	 private final String email;
	 private final AmazonSES amazonSES;
	 
	 public UserAccountSecurityOperationEvent(String email, AmazonSES amazonSES) {
		super();
		this.email = email;
		this.amazonSES = amazonSES;
	 }
	 
	 public String emailVerification(String token) {
		 try {
			 String canonicalHostName   = InetAddress.getLocalHost().getCanonicalHostName();
			 amazonSES.setCanonicalHostName(canonicalHostName);
			 amazonSES.verifyEmail(email, token);
			 return token;
		 }catch (Exception e) {
			return "error";
		}
	 }
	 
     public void passwordReset(String token,String name) {
    	 try {
    		 String canonicalHostName   = InetAddress.getLocalHost().getCanonicalHostName();
			 amazonSES.setCanonicalHostName(canonicalHostName);
    		 amazonSES.sendPasswordResetRequest(name, email, token);
    	 }catch (Exception e) {
			// TODO: handle exception
		}
	 }
	 
}
