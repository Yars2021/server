package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.util.CommandOutput;
import ru.itmo.p3114.s312198.util.command.actions.Status;

public class NoOperation extends AbstractServerCommand {
    public NoOperation() {
        super("nop", "*");
    }

    @Override
    public CommandOutput execute() {
        return new CommandOutput(Status.UNDEFINED, null);
    }
}
