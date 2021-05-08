package ru.itmo.p3114.s312198.command;

import ru.itmo.p3114.s312198.util.command.actions.AbstractCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

public class ServerCommandReader {
    private ObjectInputStream reader;
    private Socket socket;

    public ServerCommandReader(Socket clientSocket) {
        try {
            reader = new ObjectInputStream(clientSocket.getInputStream());
            socket = clientSocket;
        } catch (IOException ioe) {
            // todo
            ioe.printStackTrace();
        }
    }

    public AbstractCommand receive() throws SocketException {
        if (reader == null) {
            System.out.println("Unable to read");
            return null;
        } else {
            try {
                return (AbstractCommand) reader.readObject();
            } catch (IOException ioe) {
                throw new SocketException("Connection closed");
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
                return null;
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() {
        try {
            reader.close();
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
