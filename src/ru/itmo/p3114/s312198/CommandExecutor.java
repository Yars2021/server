package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.util.CommandOutput;
import ru.itmo.p3114.s312198.util.command.actions.AbstractCommand;

import java.net.SocketException;
import java.util.LinkedHashSet;

public class CommandExecutor {
    private Connection connection;
    private final LinkedHashSet<StudyGroup> studyGroups;

    public CommandExecutor(LinkedHashSet<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void executeCommand() throws SocketException {
        AbstractCommand command = connection.getServerCommandReader().receive();

        if (command != null) {
            System.out.println("Received: " + command.getCommand());
            command.setTargetCollection(studyGroups);
            CommandOutput commandOutput = command.execute();
            connection.getServerOutputWriter().send(commandOutput);
        }
    }
}
