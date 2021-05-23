package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.server_command.ServerCommandExecutor;
import ru.itmo.p3114.s312198.server_command.ServerCommandLineProcessor;
import ru.itmo.p3114.s312198.util.ConsoleReader;

import java.util.LinkedHashSet;

public class ProcessServerCommands implements Runnable {
    private final LinkedHashSet<StudyGroup> studyGroups;
    private boolean isRunning = true;

    public ProcessServerCommands(LinkedHashSet<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        ServerCommandLineProcessor commandLineProcessor = new ServerCommandLineProcessor();
        ServerCommandExecutor serverCommandExecutor = new ServerCommandExecutor(studyGroups);
        while (serverCommandExecutor.isRunning()) {
            serverCommandExecutor.executeCommand(commandLineProcessor.parseUserInput(ConsoleReader.readLine()));
        }
        isRunning = false;
    }
}
