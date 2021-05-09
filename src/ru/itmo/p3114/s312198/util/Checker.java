package ru.itmo.p3114.s312198.util;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Checker {
    private ServerSocket serverSocket;

    public Checker() {
    }

    public void openSocket(int port) throws BindException {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is up");
        } catch (IOException ioe) {
            throw new BindException("Port 6547 has been taken");
        }
    }

    public Socket check() {
        try {
            return serverSocket.accept();
        } catch (IOException ioe) {
            return null;
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
