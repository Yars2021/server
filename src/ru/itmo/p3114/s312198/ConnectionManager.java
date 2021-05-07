package ru.itmo.p3114.s312198;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
    private final Checker checker = new Checker();
    private final HashMap<InetAddress, Connection> connections = new HashMap<>();
    private Socket clientSocket;

    public ConnectionManager(int port) {
        checker.openSocket(port);
    }

    public void checkConnections() {
        clientSocket = checker.check();
        Connection connection;

        if (clientSocket == null) {
            System.out.println("No clients found");
        } else {
            connection = new Connection(new ServerCommandReader(clientSocket), new ServerOutputWriter(clientSocket));
            connections.put(clientSocket.getInetAddress(), connection);
        }
    }

    public Connection getConnection(InetAddress address) {
        return connections.get(address);
    }

    public HashMap<InetAddress, Connection> getFullConnections() {
        return connections;
    }

    public ArrayList<Connection> getConnections() {
        ArrayList<Connection> conn = new ArrayList<>();
        for (InetAddress address : connections.keySet()) {
            conn.add(connections.get(address));
        }

        return conn;
    }

    public void closeConnection(InetAddress address) {
        try {
            connections.get(address).close();
            checker.close();
            clientSocket.close();
            connections.remove(address);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void closeAllConnections() {
        for (InetAddress address : connections.keySet()) {
            closeConnection(address);
        }
    }
}
