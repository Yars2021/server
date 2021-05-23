package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.util.CommandOutput;

import java.io.Serializable;

public class UserSignature implements Serializable {
    private final String username;
    private final String passHash;

    public UserSignature(String username, String passHash) {
        this.username = username;
        this.passHash = passHash;
    }

    public UserSignature(CommandOutput authorizationData) {
        if (authorizationData.getOutput() != null) {
            username = authorizationData.getOutput().get(0);
            passHash = authorizationData.getOutput().get(1);
        } else {
            username = passHash = null;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassHash() {
        return passHash;
    }
}
