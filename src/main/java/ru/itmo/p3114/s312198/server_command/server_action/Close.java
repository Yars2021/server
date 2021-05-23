package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.UserHashMap;
import ru.itmo.p3114.s312198.util.CommandOutput;
import ru.itmo.p3114.s312198.util.command.actions.Status;

import java.util.ArrayList;

public class Close extends AbstractServerCommand {
    public Close() {
        super("close", ".*");
    }

    public Close(ArrayList<String> arguments) {
        this();
        setArguments(arguments);
    }

    @Override
    public CommandOutput execute() {
        ArrayList<String> output = new ArrayList<>();
        Kick kick = new Kick(UserHashMap.getKeyList());
        kick.execute();
        output.add("All connections are closed, preparing to shut down");
        new Persist().execute();
        return new CommandOutput(Status.OK, output);
    }
}
