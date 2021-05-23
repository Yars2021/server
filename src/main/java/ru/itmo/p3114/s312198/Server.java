package ru.itmo.p3114.s312198;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.collection.StudyGroup;

import java.util.LinkedHashSet;

public class Server {
    static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();
        Thread serverCommandThread, clientListenerThread;

        ProcessServerCommands processServerCommands = new ProcessServerCommands(studyGroups);
        ListenForClients listenForClients = new ListenForClients(6547, studyGroups);

        serverCommandThread = new Thread(processServerCommands);
        serverCommandThread.setName("SRV_PROC");

        clientListenerThread = new Thread(listenForClients);
        clientListenerThread.setName("SRV_LSNR");

        serverCommandThread.start();
        clientListenerThread.start();
    }
}
