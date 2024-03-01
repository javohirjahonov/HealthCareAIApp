package com.example.HealthCare.exception;


public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message){
        super(message);
    }
 }
