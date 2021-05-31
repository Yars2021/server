package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.command.actions.Status;

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
        if (synchronizedCollectionManager == null) {
            return new CommandOutput(Status.FAILED, null);
        } else {
            synchronizedCollectionManager.load();
            return new CommandOutput(Status.OK, null);
        }
    }
}