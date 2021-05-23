package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.collection.StudyGroup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.concurrent.ForkJoinPool;

public class ListenForClients implements Runnable {
    private final ForkJoinPool forkJoinPool = new ForkJoinPool();
    private final LinkedHashSet<StudyGroup> studyGroups;
    private final int port;
    volatile private boolean running = true;

    public ListenForClients(int port, LinkedHashSet<StudyGroup> studyGroups) {
        this.port = port;
        this.studyGroups = studyGroups;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        Socket clientSocket;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                clientSocket = serverSocket.accept();
                System.out.println("A new connection has been created: " + clientSocket);
                ClientTask clientTask = new ClientTask(clientSocket, studyGroups);
                forkJoinPool.execute(clientTask);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
