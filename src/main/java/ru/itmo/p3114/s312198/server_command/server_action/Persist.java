package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.util.CommandOutput;

import java.util.ArrayList;

public class Persist extends AbstractServerCommand {
    public Persist() {
        super("persist", ".*");
    }

    public Persist(ArrayList<String> arguments) {
        this();
        setArguments(arguments);
    }

    @Override
    public CommandOutput execute() {
        //todo
        return null;
    }
}
