package ru.itmo.p3114.s312198.exception;

public class DBBlockedActionException extends RuntimeException {
    public DBBlockedActionException() {
        super("Access denied");
    }

    public DBBlockedActionException(String msg) {
        super("Access denied: " + msg);
    }
}
