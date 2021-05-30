package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.util.UserHashMap;
import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.command.actions.Status;

import java.util.ArrayList;

public class ShowConnections extends AbstractServerCommand {
    public ShowConnections() {
        super("show_connections", ".*");
    }

    public ShowConnections(ArrayList<String> arguments) {
        this();
        setArguments(arguments);
    }

    @Override
    public CommandOutput execute() {
        ArrayList<String> output = new ArrayList<>();
        if (UserHashMap.size() == 0) {
            output.add("Connection list is empty");
        } else {
            output.add("Current connection list: ");
            for (String username : UserHashMap.getKeys()) {
                output.add(UserHashMap.get(username).toString(1));
            }
        }
        return new CommandOutput(Status.OK, output);
    }
}
