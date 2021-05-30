package ru.itmo.p3114.s312198.task;

import ru.itmo.p3114.s312198.util.SynchronizedCollectionManager;
import ru.itmo.p3114.s312198.collection.StudyGroup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.concurrent.ForkJoinPool;

public class ListenForClients implements Runnable {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private ServerSocket serverSocket;
    private boolean running = true;

    public ListenForClients(int port, LinkedHashSet<StudyGroup> studyGroups) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioe) {
            this.serverSocket = null;
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {
        SynchronizedCollectionManager synchronizedCollectionManager = new SynchronizedCollectionManager();
        Socket clientSocket;

        if (serverSocket != null) {
            synchronizedCollectionManager.clear();
            try {
                while (running) {
                    clientSocket = serverSocket.accept();
                    System.out.println("A new connection has been created: " + clientSocket);
                    ClientTask clientTask = new ClientTask(clientSocket, synchronizedCollectionManager);
                    forkJoinPool.execute(clientTask);
                }
            } catch (IOException ioe) {
                System.out.println("Shutting down the server socket");
            }
        }
        forkJoinPool.shutdown();
        Thread.currentThread().interrupt();
    }
}
