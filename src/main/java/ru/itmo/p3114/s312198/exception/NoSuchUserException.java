package ru.itmo.p3114.s312198.exception;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException() {
        super("No such user found in the connection list");
    }

    public NoSuchUserException(String username) {
        super("User \"" + username + "\" is not connected");
    }
}
