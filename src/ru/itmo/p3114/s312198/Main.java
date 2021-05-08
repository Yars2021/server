package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandExecutor;
import ru.itmo.p3114.s312198.util.Connection;
import ru.itmo.p3114.s312198.util.ConnectionManager;

import java.net.SocketException;
import java.util.LinkedHashSet;

public class Main {
    public static void main(String[] args) {
        LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();
        CommandExecutor commandExecutor = new CommandExecutor(studyGroups);
        ConnectionManager connectionManager = new ConnectionManager(6547);

        boolean connected;

        while (true) {
            connectionManager.checkConnections();
            connected = true;

            while (connected) {
                for (Connection connection : connectionManager.getConnections()) {
                    try {
                        commandExecutor.setConnection(connection);
                        commandExecutor.executeCommand();
                    } catch (SocketException se) {
                        System.out.println("Connection " + connection.getServerCommandReader().getSocket() + " has been closed");
                        connectionManager.closeConnection(connection.getServerCommandReader().getSocket());
                        connected = false;
                    }
                }
            }
        }
    }
}
