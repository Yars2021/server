package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.UserHashMap;
import ru.itmo.p3114.s312198.exception.NoSuchUserException;
import ru.itmo.p3114.s312198.util.CommandOutput;
import ru.itmo.p3114.s312198.util.command.actions.Status;

import java.io.IOException;
import java.util.ArrayList;

public class Kick extends AbstractServerCommand {
    public Kick() {
        super("kick", "((\\w+)\\s*?)+");
    }

    public Kick(ArrayList<String> arguments) {
        this();
        setArguments(arguments);
    }

    @Override
    public CommandOutput execute() {
        if (arguments == null || arguments.size() == 0) {
            return new CommandOutput(Status.INCORRECT_ARGUMENTS, null);
        } else {
            ArrayList<String> output = new ArrayList<>();
            for (String arg : arguments) {
                try {
                    UserHashMap.get(arg).getClientSocket().shutdownInput();
                    UserHashMap.get(arg).getClientSocket().shutdownOutput();
                    UserHashMap.get(arg).getClientSocket().close();
                    UserHashMap.remove(arg);
                } catch (IOException ioe) {
                    output.add("Unable to kick a user: Unexpected socket exception");
                } catch (NoSuchUserException nue) {
                    output.add("Unable to kick a user: " + nue.getMessage());
                }
            }
            return new CommandOutput(Status.OK, output);
        }
    }
}
