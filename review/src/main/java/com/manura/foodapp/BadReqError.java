/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.manura.foodapp;

/**
 *
 * @author Manura Sanjula
 */
public class BadReqError extends RuntimeException{
     public BadReqError(String name){
         super(name);
     }
}
