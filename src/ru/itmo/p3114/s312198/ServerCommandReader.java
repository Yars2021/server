package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.util.command.actions.AbstractCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerCommandReader {
    private ObjectInputStream reader;

    public ServerCommandReader(Socket clientSocket) {
        try {
            reader = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException ioe) {
            // todo
            ioe.printStackTrace();
        }
    }

    public AbstractCommand receive() {
        if (reader == null) {
            System.out.println("Unable to read");
            return null;
        } else {
            try {
                return (AbstractCommand) reader.readObject();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return null;
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                return null;
            }
        }
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
