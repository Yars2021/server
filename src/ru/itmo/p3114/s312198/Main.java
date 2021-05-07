package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;

import java.util.LinkedHashSet;

public class Main {
    public static void main(String[] args) {
        LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();
        ConnectionManager connectionManager = new ConnectionManager(6547);

        CommandExecutor commandExecutor = new CommandExecutor(studyGroups);
        connectionManager.checkConnections();

        for (Connection connection : connectionManager.getConnections()) {
            commandExecutor.setConnection(connection);
            commandExecutor.executeCommand();
        }

        connectionManager.closeAllConnections();
    }
}
