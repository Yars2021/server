package ru.itmo.p3114.s312198;

import java.io.Serializable;

public class AuthorizationRequest implements Serializable {
    private final String type;
    private final UserSignature userSignature;

    public AuthorizationRequest(String type, UserSignature userSignature) {
        this.type = type;
        this.userSignature = userSignature;
    }

    public String getType() {
        return type;
    }

    public UserSignature getUserSignature() {
        return userSignature;
    }
}
