package ru.itmo.p3114.s312198.server_command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.server_command.server_action.AbstractServerCommand;

import java.util.LinkedHashSet;

public class ServerCommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ServerCommandExecutor.class);
    private final LinkedHashSet<StudyGroup> collectionCopy;
    private boolean running = true;

    public ServerCommandExecutor(LinkedHashSet<StudyGroup> collection) {
        collectionCopy = collection;
    }

    public void executeCommand(AbstractServerCommand command) {
        command.setCollection(collectionCopy);
        CommandOutput commandOutput = command.execute();

        switch (commandOutput.getStatus()) {
            case OK:
            case UNDEFINED:
                logger.info("Command \"" + command.getCommand() + "\" executed successfully");
                break;
            case FAILED:
            case INCORRECT_ARGUMENTS:
                logger.error("Command \"" + command.getCommand() + "\" failed to execute. Status: " + commandOutput.getStatus());
                break;
        }

        if (commandOutput.getOutput() != null) {
            for (String outputLine : commandOutput.getOutput()) {
                System.out.println(outputLine);
            }
        }

        if ("close".equals(command.getCommand())) {
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }
}
