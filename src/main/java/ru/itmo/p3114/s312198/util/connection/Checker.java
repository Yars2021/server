package ru.itmo.p3114.s312198.util.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Checker {
    static final Logger logger = LoggerFactory.getLogger(Checker.class);
    private ServerSocket serverSocket;

    public Checker() {
    }

    public void openSocket(int port) throws BindException {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server is up");
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
