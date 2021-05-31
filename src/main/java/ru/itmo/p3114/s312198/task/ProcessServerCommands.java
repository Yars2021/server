package ru.itmo.p3114.s312198.task;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.server_command.ServerCommandExecutor;
import ru.itmo.p3114.s312198.server_command.ServerCommandLineProcessor;
import ru.itmo.p3114.s312198.util.ConsoleReader;
import ru.itmo.p3114.s312198.util.SynchronizedCollectionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedHashSet;

public class ProcessServerCommands implements Runnable {
    private final SynchronizedCollectionManager synchronizedCollectionManager;
    private final LinkedHashSet<StudyGroup> studyGroups;
    private final ServerSocket serverSocket;

    public ProcessServerCommands(LinkedHashSet<StudyGroup> studyGroups, ServerSocket serverSocket, SynchronizedCollectionManager synchronizedCollectionManager) {
        this.studyGroups = studyGroups;
        this.serverSocket = serverSocket;
        this.synchronizedCollectionManager = synchronizedCollectionManager;
    }

    @Override
    public void run() {
        synchronizedCollectionManager.load();
        ServerCommandLineProcessor commandLineProcessor = new ServerCommandLineProcessor();
        ServerCommandExecutor serverCommandExecutor = new ServerCommandExecutor(studyGroups);
        System.out.println("Server is up");
        while (serverCommandExecutor.isRunning()) {
            serverCommandExecutor.executeCommand(commandLineProcessor.parseUserInput(ConsoleReader.readLine()), synchronizedCollectionManager);
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
