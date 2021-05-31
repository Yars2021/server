package ru.itmo.p3114.s312198.db;

public class AccountData {
    private final AuthorizationStatus authorizationStatus;
    private final long accountId;

    public AccountData(AuthorizationStatus authorizationStatus, long accountId) {
        this.authorizationStatus = authorizationStatus;
        this.accountId = accountId;
    }

    public AuthorizationStatus getAuthorizationStatus() {
        return authorizationStatus;
    }

    public long getAccountId() {
        return accountId;
    }
}
