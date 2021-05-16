package ru.itmo.p3114.s312198.util.connection;

import ru.itmo.p3114.s312198.client_interaction.ServerCommandReader;
import ru.itmo.p3114.s312198.client_interaction.ServerOutputWriter;

public class Connection {
    private ServerCommandReader serverCommandReader;
    private ServerOutputWriter serverOutputWriter;

    public Connection(ServerCommandReader serverCommandReader, ServerOutputWriter serverOutputWriter) {
        this.serverCommandReader = serverCommandReader;
        this.serverOutputWriter = serverOutputWriter;
    }

    public void setServerCommandReader(ServerCommandReader serverCommandReader) {
        this.serverCommandReader = serverCommandReader;
    }

    public void setServerOutputWriter(ServerOutputWriter serverOutputWriter) {
        this.serverOutputWriter = serverOutputWriter;
    }

    public ServerCommandReader getServerCommandReader() {
        return serverCommandReader;
    }

    public ServerOutputWriter getServerOutputWriter() {
        return serverOutputWriter;
    }

    public void close() {
        serverCommandReader.close();
        serverOutputWriter.close();
    }
}
