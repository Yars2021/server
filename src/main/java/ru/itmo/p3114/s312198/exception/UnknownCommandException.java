package ru.itmo.p3114.s312198.exception;

public class UnknownCommandException extends RuntimeException {
    public UnknownCommandException() {
        super("No such command");
    }

    public UnknownCommandException(String message) {
        super(message);
    }
}
