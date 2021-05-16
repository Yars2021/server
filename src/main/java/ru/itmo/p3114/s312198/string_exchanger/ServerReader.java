package ru.itmo.p3114.s312198.string_exchanger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerReader {
    private BufferedReader reader;

    public ServerReader(Socket clientSocket) {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ioe) {
            // todo
            ioe.printStackTrace();
        }
    }

    public String readLine() {
        if (reader == null) {
            System.out.println("Unable to read");
            return null;
        } else {
            try {
                return reader.readLine();
            } catch (IOException ioe) {
                ioe.printStackTrace();
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
