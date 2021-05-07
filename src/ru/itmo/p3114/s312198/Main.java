package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.util.command.actions.AbstractCommand;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashSet;

public class Main {
    public static void main(String[] args) {
        Checker checker = new Checker();
        checker.openSocket(6547);
        Socket clientSocket = checker.check();
        ServerCommandReader serverCommandReader = new ServerCommandReader(clientSocket);
        ServerWriter serverWriter = new ServerWriter(clientSocket);

        LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();

        try {
            try {
                try {
                    while (true) {
                        AbstractCommand command = serverCommandReader.receive();
                        if (command != null) {
                            System.out.println(command.getCommand());
                            command.setTargetCollection(studyGroups);
                            command.execute();
                        }
                        serverWriter.writeLine("From server");
                    }
                } finally {
                    clientSocket.close();
                    serverCommandReader.close();
                }
            } finally {
                System.out.println("Server closed");
                checker.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
