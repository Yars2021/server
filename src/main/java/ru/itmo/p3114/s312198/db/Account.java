package ru.itmo.p3114.s312198.db;

import java.io.Serializable;

public class Account implements Serializable {
    private Long id;
    private String login;
    private String credentials;

    public Account() {
    }

    public Account(Long id, String login, String credentials) {
        this.id = id;
        this.login = login;
        this.credentials = credentials;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getCredentials() {
        return credentials;
    }
}
