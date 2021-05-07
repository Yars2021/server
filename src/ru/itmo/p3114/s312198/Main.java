package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;

import java.util.LinkedHashSet;

public class Main {
    public static void main(String[] args) {
        LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();

        CommandExecutor commandExecutor = new CommandExecutor(studyGroups);
        commandExecutor.connect(6547);
        commandExecutor.executeCommand();
        commandExecutor.closeConnection();
    }
}
