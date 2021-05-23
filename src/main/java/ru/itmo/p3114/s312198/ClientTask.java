package ru.itmo.p3114.s312198;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.util.CommandOutput;
import ru.itmo.p3114.s312198.util.command.actions.AbstractCommand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedHashSet;

public class ClientTask implements Runnable {
    static final Logger logger = LoggerFactory.getLogger(ClientTask.class);

    private final LinkedHashSet<StudyGroup> studyGroups;
    private final Socket socket;

    public ClientTask(Socket socket, LinkedHashSet<StudyGroup> studyGroups) {
        this.socket = socket;
        this.studyGroups = studyGroups;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            AuthorizationResponse authorizationResponse;
            boolean allowed = false;
            String username = "";
            try {
                AuthorizationRequest authorizationRequest = (AuthorizationRequest) objectInputStream.readObject();
                // todo Check the account database
                authorizationResponse = new
                        AuthorizationResponse(true, "Welcome, " + authorizationRequest.getUserSignature().getUsername());
                username = authorizationRequest.getUserSignature().getUsername();
                UserHashMap.add(new User(socket, username));
                allowed = true;
            } catch (ClassNotFoundException ce) {
                authorizationResponse = new AuthorizationResponse(false, "Unable to authorize");
            }
            objectOutputStream.writeObject(authorizationResponse);
            if (allowed) {
                boolean running = true;
                while (running) {
                    try {
                        ClientDataPacket clientDataPacket = (ClientDataPacket) objectInputStream.readObject();
                        AbstractCommand command = clientDataPacket.getCommand();
                        command.setTargetCollection(studyGroups);
                        CommandOutput commandOutput = command.execute();
                        objectOutputStream.writeObject(commandOutput);
                        switch (commandOutput.getStatus()) {
                            case OK: case UNDEFINED:
                                logger.info("Command \"" + command.getCommand() + "\" executed successfully");
                                break;
                            case FAILED: case INCORRECT_ARGUMENTS:
                                logger.error("Command \"" + command.getCommand() + "\" executed with status \"" + commandOutput.getStatus() + "\"");
                                break;
                        }
                        if ("exit".equals(command.getCommand())) {
                            running = false;
                        }
                    } catch (ClassNotFoundException ce) {
                        ce.printStackTrace();
                    }
                }
            }
            if (UserHashMap.containsUsername(username)) {
                UserHashMap.remove(username);
            }
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
