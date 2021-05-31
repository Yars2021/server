package ru.itmo.p3114.s312198.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.db.DBHelper;
import ru.itmo.p3114.s312198.db.DBICommandValidator;
import ru.itmo.p3114.s312198.exception.UnknownCommandException;
import ru.itmo.p3114.s312198.server_command.server_action.Persist;
import ru.itmo.p3114.s312198.util.SynchronizedCollectionManager;
import ru.itmo.p3114.s312198.util.UserHashMap;
import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.command.actions.AbstractCommand;
import ru.itmo.p3114.s312198.server_command.AuthorizationStatus;
import ru.itmo.p3114.s312198.transmission.CSChannel;
import ru.itmo.p3114.s312198.transmission.structures.authorization.AuthorizationRequest;
import ru.itmo.p3114.s312198.transmission.structures.authorization.AuthorizationResponse;
import ru.itmo.p3114.s312198.transmission.structures.packet.ClientDataPacket;
import ru.itmo.p3114.s312198.transmission.structures.packet.ServerDataPacket;
import ru.itmo.p3114.s312198.transmission.structures.user.User;

import java.io.IOException;
import java.net.Socket;

public class ClientTask implements Runnable {
    static final Logger logger = LoggerFactory.getLogger(ClientTask.class);

    private final Socket socket;
    private final SynchronizedCollectionManager synchronizedCollectionManager;

    public ClientTask(Socket socket, SynchronizedCollectionManager synchronizedCollectionManager) {
        this.socket = socket;
        this.synchronizedCollectionManager = synchronizedCollectionManager;
    }

    @Override
    public void run() {
        long accountID = -1;
        try (CSChannel channel = new CSChannel(socket)) {
            AuthorizationResponse authorizationResponse;
            AuthorizationStatus authorizationStatus = AuthorizationStatus.UNDEFINED;
            String username = "";
            try {
                AuthorizationRequest authorizationRequest = (AuthorizationRequest) channel.read();
                authorizationStatus = new DBICommandValidator().authorize(authorizationRequest, accountID);
                if (authorizationStatus == AuthorizationStatus.ALLOWED) {
                    authorizationResponse = new
                            AuthorizationResponse(true, AuthorizationStatus.ALLOWED.getMsg() +
                            ", " + authorizationRequest.getUserSignature().getUsername());
                    username = authorizationRequest.getUserSignature().getUsername();
                    UserHashMap.add(new User(socket, username));
                } else {
                    authorizationResponse = new AuthorizationResponse(false, authorizationStatus.getMsg());
                }
            } catch (ClassNotFoundException ce) {
                authorizationResponse = new AuthorizationResponse(false, AuthorizationStatus.UNDEFINED.getMsg());
            }
            channel.write(authorizationResponse);
            if (authorizationStatus == AuthorizationStatus.ALLOWED) {
                boolean running = true;
                while (running) {
                    try {
                        ClientDataPacket clientDataPacket = (ClientDataPacket) channel.read();
                        AbstractCommand command = clientDataPacket.getCommand();
                        CommandOutput commandOutput = synchronizedCollectionManager.execute(command, accountID);
                        channel.write(new ServerDataPacket("Executed", commandOutput, true));
                        switch (commandOutput.getStatus()) {
                            case OK:
                            case UNDEFINED:
                                logger.info("Command \"" + command.getCommand() + "\" executed successfully");
                                break;
                            case FAILED:
                            case INCORRECT_ARGUMENTS:
                                logger.error("Command \"" + command.getCommand() + "\" executed with status \"" + commandOutput.getStatus() + "\"");
                                break;
                        }
                        if ("exit".equals(command.getCommand())) {
                            running = false;
                        }
                    } catch (ClassNotFoundException ce) {
                        channel.write(new ServerDataPacket("The data packet has been damaged", null, false));
                    } catch (UnknownCommandException uce) {
                        channel.write(new ServerDataPacket("No such command", null, false));
                    }
                }
            }
            if (UserHashMap.containsUsername(username)) {
                UserHashMap.remove(username);
            }
        } catch (IOException ioe) {
            System.out.println("Unexpected connection loss");
        }
        System.out.println("Connection " + socket + " has been closed");
    }
}
