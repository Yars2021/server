package ru.itmo.p3114.s312198.server_command;

public enum AuthorizationStatus {
    UNDEFINED("Unable to authorize"),
    ALLOWED("Welcome"),
    INCORRECT_CREDENTIALS("Incorrect credentials"),
    USERNAME_IS_TAKEN("This username has been taken, try using another one"),
    BANNED("You are banned from the server");

    private final String msg;

    AuthorizationStatus(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
