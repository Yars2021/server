package ru.itmo.p3114.s312198.util.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.client_interaction.ServerCommandReader;
import ru.itmo.p3114.s312198.client_interaction.ServerOutputWriter;
import ru.itmo.p3114.s312198.util.user.User;
import ru.itmo.p3114.s312198.util.user.UserHashMap;

import java.io.IOException;
import java.net.BindException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionManager {
    static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private final Checker checker = new Checker();
    private final HashMap<Socket, Connection> connections = new HashMap<>();

    public ConnectionManager(int port) throws BindException {
        checker.openSocket(port);
    }

    public void checkConnections() {
        Socket clientSocket = checker.check();
        Connection connection;

        if (clientSocket == null) {
            // todo
        } else {
            logger.info("Current socket: " + clientSocket);
            connection = new Connection(new ServerCommandReader(clientSocket), new ServerOutputWriter(clientSocket));
            UserHashMap.add(new User("1", connection));
            connections.put(clientSocket, connection);
        }
    }

    public Connection getConnection(Socket socket) {
        return connections.get(socket);
    }

    public HashMap<Socket, Connection> getFullConnections() {
        return connections;
    }

    public ArrayList<Connection> getConnections() {
        ArrayList<Connection> conn = new ArrayList<>();
        for (Socket socket : connections.keySet()) {
            conn.add(connections.get(socket));
        }

        return conn;
    }

    public void closeConnection(Socket socket) {
        try {
            connections.get(socket).close();
            socket.close();
            connections.remove(socket);
        } catch (IOException ignored) {
        }
    }

    public void closeAllConnections() {
        for (Socket socket : connections.keySet()) {
            closeConnection(socket);
        }
    }
}
