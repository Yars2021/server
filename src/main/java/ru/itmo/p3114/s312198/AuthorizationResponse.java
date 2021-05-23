package ru.itmo.p3114.s312198;

import java.io.Serializable;

public class AuthorizationResponse implements Serializable {
    private final boolean isAllowed;
    private final String serverMessage;

    public AuthorizationResponse(boolean allowed, String serverMessage) {
        this.isAllowed = allowed;
        this.serverMessage = serverMessage;
    }

    public boolean isAllowed() {
        return isAllowed;
    }

    public String getServerMessage() {
        return serverMessage;
    }
}
