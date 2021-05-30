package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.command.CommandOutput;

import java.util.ArrayList;

public class Load extends AbstractServerCommand {
    public Load() {
        super("load", ".*");
    }

    public Load(ArrayList<String> arguments) {
        this();
        setArguments(arguments);
    }

    @Override
    public CommandOutput execute() {
        //todo
        return null;
    }
}
