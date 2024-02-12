package ru.hse.server.exception;

public class UserAlreadyExistExceprion extends RuntimeException {
    public UserAlreadyExistExceprion(String message){
        super(message);
    }
}
