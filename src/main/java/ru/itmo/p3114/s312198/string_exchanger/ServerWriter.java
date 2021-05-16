package ru.itmo.p3114.s312198.string_exchanger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerWriter {
    private BufferedWriter writer;

    public ServerWriter(Socket clientSocket) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException ioe) {
            // todo
            ioe.printStackTrace();
        }
    }

    public void writeLine(String line) {
        if (writer == null) {
            System.out.println("Unable to write");
        } else {
            try {
                writer.write(line + '\n');
                writer.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
