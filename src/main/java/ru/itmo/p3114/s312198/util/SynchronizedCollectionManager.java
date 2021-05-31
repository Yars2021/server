package ru.itmo.p3114.s312198.util;

import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandOutput;
import ru.itmo.p3114.s312198.command.actions.AbstractCommand;
import ru.itmo.p3114.s312198.command.actions.marker.CollectionInteracting;
import ru.itmo.p3114.s312198.command.actions.marker.DatabaseInteracting;
import ru.itmo.p3114.s312198.db.DBHelper;
import ru.itmo.p3114.s312198.db.DBICommandValidator;
import ru.itmo.p3114.s312198.db.ValidationVerdict;
import ru.itmo.p3114.s312198.exception.DBBlockedActionException;
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

    public void load() {
        DBHelper dbHelper = new DBHelper();
        clear();
        for (StudyGroup studyGroup : dbHelper.getStudyGroups()) {
            add(studyGroup);
        }
    }

    public void persist() {
        DBHelper dbHelper = new DBHelper();
        dbHelper.loadStudyGroups(studyGroups);
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

    public CommandOutput execute(AbstractCommand command, long creator) throws UnknownCommandException, DBBlockedActionException {
        if (command == null || command.getCommand() == null) {
            throw new UnknownCommandException();
        }
        if (command instanceof CollectionInteracting) {
            CommandOutput commandOutput;
            reentrantLock.lock();
            if (command instanceof DatabaseInteracting) {
                ValidationVerdict validationVerdict = new DBICommandValidator().validate(command, creator, this);
                if (validationVerdict.isSuccess()) {
                    command.setTargetCollection(studyGroups);
                    command.setId(validationVerdict.getNewId());
                    commandOutput = command.execute();
                } else {
                    throw new DBBlockedActionException();
                }
            } else {
                command.setTargetCollection(studyGroups);
                commandOutput = command.execute();
            }
            reentrantLock.unlock();
            return commandOutput;
        } else {
            return command.execute();
        }
    }
}
