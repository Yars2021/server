package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandExecutor;
import ru.itmo.p3114.s312198.file.DataFileReader;
import ru.itmo.p3114.s312198.util.Connection;
import ru.itmo.p3114.s312198.util.ConnectionManager;
import ru.itmo.p3114.s312198.util.command.actions.Save;

import java.net.BindException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Main {
    public static void main(String[] args) {
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

            System.out.println("Collection is going to be saved into \"" + filename + "\"");

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
                            System.out.println("Connection " + connection.getServerCommandReader().getSocket() + " has been closed");
                            connectionManager.closeConnection(connection.getServerCommandReader().getSocket());
                            connected = false;
                        }
                    }
                }
            }
        } catch(BindException be) {
            System.out.println(be.getMessage());
            System.out.println("Shutting down");
        }
    }
}
