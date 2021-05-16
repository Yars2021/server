package ru.itmo.p3114.s312198;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.client_interaction.CommandExecutor;
import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.file.DataFileReader;
import ru.itmo.p3114.s312198.server_command.ServerCommandExecutor;
import ru.itmo.p3114.s312198.server_command.ServerCommandLineProcessor;
import ru.itmo.p3114.s312198.util.ConsoleReader;
import ru.itmo.p3114.s312198.util.command.actions.Save;
import ru.itmo.p3114.s312198.util.connection.Connection;
import ru.itmo.p3114.s312198.util.connection.ConnectionManager;

import java.net.BindException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Server {
    static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        Runnable clientInteract = () -> {
            try {
                ConnectionManager connectionManager = new ConnectionManager(6547);
                LinkedHashSet<StudyGroup> studyGroups;
                DataFileReader dataFileReader = new DataFileReader();

                String filename = "collection_data";

                if (args.length > 0) {
                    filename = args[0];
                }

                studyGroups = dataFileReader.getData(filename);

                CommandExecutor commandExecutor = new CommandExecutor(studyGroups);

                ArrayList<String> arguments = new ArrayList<>();
                arguments.add(filename);
                Save save = new Save(arguments, studyGroups);

                logger.info("Collection is going to be saved into \"" + filename + "\"");

                boolean connected;

                while (true) {
                    connectionManager.checkConnections();
                    connected = true;

                    while (connected) {
                        for (Connection connection : connectionManager.getConnections()) {
                            try {
                                commandExecutor.setConnection(connection);
                                commandExecutor.executeCommand();
                                save.execute();
                            } catch (SocketException se) {
                                logger.info("Connection " + connection.getServerCommandReader().getSocket() + " has been closed");
                                connectionManager.closeConnection(connection.getServerCommandReader().getSocket());
                                connected = false;
                            }
                        }
                    }
                }
            } catch(BindException be) {
                logger.error(ExceptionUtils.getStackTrace(be));
                logger.info("Shutting down");
            }
        };

        Runnable serverCommand = () -> {
            ServerCommandLineProcessor commandLineProcessor = new ServerCommandLineProcessor();
            LinkedHashSet<StudyGroup> collection = new LinkedHashSet<>();
            ServerCommandExecutor serverCommandExecutor = new ServerCommandExecutor(collection);
            while (serverCommandExecutor.isRunning()) {
                serverCommandExecutor.executeCommand(commandLineProcessor.parseUserInput(ConsoleReader.readLine()));
            }
        };

        Thread clientInteractThread = new Thread(clientInteract);
        Thread serverCommandThread = new Thread(serverCommand);


        clientInteractThread.start();
        serverCommandThread.start();
    }
}
