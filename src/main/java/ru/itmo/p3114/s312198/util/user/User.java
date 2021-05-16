package ru.itmo.p3114.s312198.util.user;

import ru.itmo.p3114.s312198.util.connection.Connection;

public class User {
    private final String username;
    private final Connection connection;

    public User(String username, Connection connection) {
        this.username = username;
        this.connection = connection;
    }

    public String getUsername() {
        return username;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return "Username: " + username + "\nSocket:\n\tAddress: " + connection.getServerCommandReader().getSocket().getInetAddress().toString() +
                "\n\tPort: " + connection.getServerCommandReader().getSocket().getPort() + "\n\tLocal port: " +
                connection.getServerCommandReader().getSocket().getLocalPort() + "\n";
    }
}
