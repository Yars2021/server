package ru.itmo.p3114.s312198.util;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CollectionInteracting;
import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.command.actions.AbstractCommand;
import ru.itmo.p3114.s312198.exception.UnknownCommandException;

import java.util.LinkedHashSet;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedCollectionManager {
    private final LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();
    private final ReentrantLock reentrantLock = new ReentrantLock();

    public void clear() {
        reentrantLock.lock();
        studyGroups.clear();
        reentrantLock.unlock();
    }

    public void add(StudyGroup studyGroup) {
        reentrantLock.lock();
        studyGroups.add(studyGroup);
        reentrantLock.unlock();
    }

    public boolean contains(StudyGroup studyGroup) {
        reentrantLock.lock();
        boolean c = studyGroups.contains(studyGroup);
        reentrantLock.unlock();
        return c;
    }

    public void remove(StudyGroup studyGroup) {
        reentrantLock.lock();
        studyGroups.remove(studyGroup);
        reentrantLock.unlock();
    }

    public LinkedHashSet<StudyGroup> getCollection() {
        return studyGroups;
    }

    public CommandOutput execute(AbstractCommand command) throws UnknownCommandException {
        if (command == null || command.getCommand() == null) {
            throw new UnknownCommandException();
        }
        if (command instanceof CollectionInteracting) {
            reentrantLock.lock();
            command.setTargetCollection(studyGroups);
            CommandOutput commandOutput = command.execute();
            reentrantLock.unlock();
            return commandOutput;
        } else {
            return command.execute();
        }
    }
}
