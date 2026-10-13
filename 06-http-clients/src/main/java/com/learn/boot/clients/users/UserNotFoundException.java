package com.learn.boot.clients.users;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super("User " + id + " not found upstream");
    }
}
