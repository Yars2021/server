package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.task.ListenForClients;
import ru.itmo.p3114.s312198.task.ProcessServerCommands;
import ru.itmo.p3114.s312198.util.SynchronizedCollectionManager;

import java.util.LinkedHashSet;

public class Server {
    public static void main(String[] args) {
        SynchronizedCollectionManager synchronizedCollectionManager = new SynchronizedCollectionManager();
        synchronizedCollectionManager.load();
        int port = 6547;
        LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();
        Thread serverCommandThread, clientListenerThread;

        ListenForClients listenForClients = new ListenForClients(port, studyGroups, synchronizedCollectionManager);
        ProcessServerCommands processServerCommands = new ProcessServerCommands(studyGroups, listenForClients.getServerSocket(), synchronizedCollectionManager);

        clientListenerThread = new Thread(listenForClients);
        clientListenerThread.setName("SRV_LSNR");

        serverCommandThread = new Thread(processServerCommands);
        serverCommandThread.setName("SRV_PROC");

        clientListenerThread.start();
        serverCommandThread.start();
    }
}
