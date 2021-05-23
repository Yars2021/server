package ru.itmo.p3114.s312198;

import java.io.Serializable;
import java.net.Socket;

public class User implements Serializable {
    private final Socket clientSocket;
    private final String username;

    public User(Socket clientSocket, String username) {
        this.clientSocket = clientSocket;
        this.username = username;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username + ":\n\tSocket:\n\t\tAddress: " + clientSocket.getInetAddress().toString() +
                "\n\t\tPort: " + clientSocket.getPort() + "\n\t\tLocal port: " + clientSocket.getLocalPort();
    }

    public String toString(int indentLength) {
        String indent = "";
        for (int i = 0; i < indentLength; i++) {
            indent += '\t';
        }
        return indent + username + ":\n\t" + indent + "Socket:\n\t\t" + indent + "Address: " + clientSocket.getInetAddress().toString() +
                "\n\t\t" + indent + "Port: " + clientSocket.getPort() + "\n\t\t" + indent + "Local port: " +
                clientSocket.getLocalPort();
    }
}
