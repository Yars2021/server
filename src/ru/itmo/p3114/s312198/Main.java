package ru.itmo.p3114.s312198;

import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;
import ru.itmo.p3114.s312198.util.command.actions.AbstractCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Checker checker = new Checker();
        checker.openSocket(6547);
        Socket clientSocket = checker.check();
        ServerCommandReader serverCommandReader = new ServerCommandReader(clientSocket);

        try {
            try {
                try {
                    AbstractCommand command = serverCommandReader.receive();
                    System.out.println(command.getArguments().get(0));
                    System.out.println(command.getArguments().get(1));
                    System.out.println(command.getArguments().get(2));
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
