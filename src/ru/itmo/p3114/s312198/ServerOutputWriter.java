package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.util.CommandOutput;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerOutputWriter {
    private ObjectOutputStream writer;
    private Socket socket;

    public ServerOutputWriter(Socket clientSocket) {
        try {
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
            socket = clientSocket;
        } catch (IOException ioe) {
            // todo
            ioe.printStackTrace();
        }
    }

    public void send(CommandOutput output) {
        if (writer == null) {
            System.out.println("Unable to write");
        } else {
            try {
                writer.writeObject(output);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
