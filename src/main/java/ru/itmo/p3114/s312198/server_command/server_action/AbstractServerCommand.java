package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandOutput;

import java.util.ArrayList;
import java.util.LinkedHashSet;

abstract public class AbstractServerCommand {
    protected final String command;
    protected final String inputPattern;
    protected ArrayList<String> arguments;
    protected LinkedHashSet<StudyGroup> collectionCopy;

    public AbstractServerCommand(String command, String inputPattern) {
        this.command = command;
        this.inputPattern = inputPattern;
    }

    public AbstractServerCommand(String command, ArrayList<String> arguments, String inputPattern) {
        this.command = command;
        this.arguments = arguments;
        this.inputPattern = inputPattern;
    }

    public void setArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }

    public void setCollection(LinkedHashSet<StudyGroup> collection) {
        this.collectionCopy = collection;
    }

    public String getCommand() {
        return command;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public String getInputPattern() {
        return inputPattern;
    }

    abstract public CommandOutput execute();
}
