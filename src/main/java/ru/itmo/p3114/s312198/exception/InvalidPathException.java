package ru.itmo.p3114.s312198.exception;

public class InvalidPathException extends RuntimeException {
    public InvalidPathException() {
        super("Config file not found");
    }

    public InvalidPathException(String message) {
        super(message);
    }
}
