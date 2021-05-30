package ru.itmo.p3114.s312198.task;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.server_command.ServerCommandExecutor;
import ru.itmo.p3114.s312198.server_command.ServerCommandLineProcessor;
import ru.itmo.p3114.s312198.util.ConsoleReader;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashSet;

public class ProcessServerCommands implements Runnable {
    private final LinkedHashSet<StudyGroup> studyGroups;
    private final ServerSocket serverSocket;

    public ProcessServerCommands(LinkedHashSet<StudyGroup> studyGroups, ServerSocket serverSocket) {
        this.studyGroups = studyGroups;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        ServerCommandLineProcessor commandLineProcessor = new ServerCommandLineProcessor();
        ServerCommandExecutor serverCommandExecutor = new ServerCommandExecutor(studyGroups);
        System.out.println("Server is up");
        while (serverCommandExecutor.isRunning()) {
            serverCommandExecutor.executeCommand(commandLineProcessor.parseUserInput(ConsoleReader.readLine()));
        }
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
        Thread.currentThread().interrupt();
    }
}
