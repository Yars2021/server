package ru.itmo.p3114.s312198.db;

public class ValidationVerdict {
    private final boolean success;
    private final long newId;

    public ValidationVerdict(boolean success, long newId) {
        this.success = success;
        this.newId = newId;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getNewId() {
        return newId;
    }
}
