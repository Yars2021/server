package ru.itmo.p3114.s312198.server_command.server_action;

import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.command.actions.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class Help extends AbstractServerCommand {
    private final HashMap<String, String> reference = new HashMap<>();

    public Help() {
        super("help", "(((\\w+)\\s*?)+)?");
        reference.put("help", "help [arg1] [arg2] ... : shows the command reference");
        reference.put("show_connections", "show_connections: shows all the clients connected to the server");
        reference.put("kick", "kick <arg1> [arg2] ... : closes connection with a client or clients");
        reference.put("save", "save arg: saves a copy of the database into a CSV file");
        reference.put("close", "close: closes the server");
        reference.put("persist", "persist: updates the database");
        reference.put("load", "load: downloads a copy of the database into collection");
    }

    public Help(ArrayList<String> arguments) {
        this();
        setArguments(arguments);
    }

    @Override
    public CommandOutput execute() {
        ArrayList<String> output = new ArrayList<>();
        if (arguments == null) {
            output.add("╔══════════════════════════════════════════════════════════════════════════╗");
            output.add("║                        Server command reference                          ║");
            output.add("╠══════════════════════════════════════════════════════════════════════════╣");
            output.add("║ help [arg1] [arg2] ... : shows the command reference                     ║");
            output.add("║ show_connections: shows all the clients connected to the server          ║");
            output.add("║ kick <arg1> [arg2] ... : closes connection with a client or clients      ║");
            output.add("║ save arg: saves a copy of the database into a CSV file                   ║");
            output.add("║ close: closes the server                                                 ║");
            output.add("║ persist: updates the database                                            ║");
            output.add("║ load: downloads a copy of the database into collection                   ║");
            output.add("╚══════════════════════════════════════════════════════════════════════════╝");
        } else {
            for (String cmd : arguments) {
                if (reference.containsKey(cmd)) {
                    output.add(reference.get(cmd));
                } else {
                    output.add("\"" + cmd + "\" is not a valid command");
                }
            }
        }
        return new CommandOutput(Status.OK, output);
    }
}
