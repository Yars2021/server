package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.util.CommandOutput;
import ru.itmo.p3114.s312198.util.command.actions.AbstractCommand;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashSet;

public class CommandExecutor {
    private Socket clientSocket;
    private ServerCommandReader serverCommandReader;
    private ServerOutputWriter serverOutputWriter;
    private final LinkedHashSet<StudyGroup> studyGroups;

    public CommandExecutor(LinkedHashSet<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    public void connect(int port) {
        Checker checker = new Checker();
        checker.openSocket(port);
        clientSocket = checker.check();

        if (clientSocket == null) {
            System.out.println("No clients found");
        } else {
            serverCommandReader = new ServerCommandReader(clientSocket);
            serverOutputWriter = new ServerOutputWriter(clientSocket);
        }
    }

    public void executeCommand() {
        AbstractCommand command = serverCommandReader.receive();

        if (command != null) {
            command.setTargetCollection(studyGroups);
            CommandOutput commandOutput = command.execute();
            serverOutputWriter.send(commandOutput);
        }
    }

    public void closeConnection() {
        try {
            clientSocket.close();
            serverCommandReader.close();
            serverOutputWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
