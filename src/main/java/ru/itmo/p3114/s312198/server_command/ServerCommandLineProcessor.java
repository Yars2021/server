package ru.itmo.p3114.s312198.server_command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.server_command.server_action.AbstractServerCommand;
import ru.itmo.p3114.s312198.server_command.server_action.Close;
import ru.itmo.p3114.s312198.server_command.server_action.Help;
import ru.itmo.p3114.s312198.server_command.server_action.Kick;
import ru.itmo.p3114.s312198.server_command.server_action.NoOperation;
import ru.itmo.p3114.s312198.server_command.server_action.Persist;
import ru.itmo.p3114.s312198.server_command.server_action.SaveData;
import ru.itmo.p3114.s312198.server_command.server_action.ServerCommands;
import ru.itmo.p3114.s312198.server_command.server_action.ShowConnections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class ServerCommandLineProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ServerCommandLineProcessor.class);

    public AbstractServerCommand parseUserInput(String line) {
        AbstractServerCommand command = null;
        ArrayList<String> args = new ArrayList<>();
        ServerCommands cmd;

        if (line == null) {
            cmd = ServerCommands.CLOSE;
        } else {
            try {
                cmd = ServerCommands.valueOf(line.trim().split("\\s")[0].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException exc) {
                logger.error(line + " is not a valid input.\nUse \"help\" to see the command reference");
                cmd = ServerCommands.NOP;
            }
        }

        switch (cmd) {
            case NOP:
                command = new NoOperation();
                break;
            case HELP:
                if (line.trim().split("\\s").length > 1) {
                    args.addAll(Arrays.asList(line.trim().split("\\s")).subList(1, line.trim().split("\\s").length));
                    command = new Help(args);
                } else {
                    command = new Help();
                }
                break;
            case SHOW_CONNECTIONS:
                command = new ShowConnections();
                break;
            case KICK:
                if (line.trim().split("\\s").length > 1) {
                    args.addAll(Arrays.asList(line.trim().split("\\s")).subList(1, line.trim().split("\\s").length));
                } else {
                    logger.error("Incorrect input, no argument found");
                }
                command = new Kick(args);
                break;
            case SAVE:
                if (line.trim().split("\\s").length >= 2) {
                    args.addAll(Arrays.asList(line.trim().split("\\s")).subList(1, line.trim().split("\\s").length));
                    if (line.trim().split("\\s").length > 2) {
                        logger.warn("Only first argument is going to be used in this command");
                    }
                } else {
                    logger.error("Incorrect input, no argument found");
                }
                command = new SaveData(args);
                break;
            case CLOSE:
                command = new Close();
                break;
            case PERSIST:
                command = new Persist();
                break;
        }
        return command;
    }
}
