package ru.itmo.p3114.s312198.db;

import ru.itmo.p3114.s312198.collection.Location;
import ru.itmo.p3114.s312198.collection.Person;
import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandLineProcessor;
import ru.itmo.p3114.s312198.command.Commands;
import ru.itmo.p3114.s312198.command.actions.AbstractCommand;
import ru.itmo.p3114.s312198.exception.ValueOutOfBoundsException;
import ru.itmo.p3114.s312198.server_command.AuthorizationStatus;
import ru.itmo.p3114.s312198.transmission.structures.authorization.AuthorizationRequest;
import ru.itmo.p3114.s312198.util.FieldParser;
import ru.itmo.p3114.s312198.util.LocationBuilder;
import ru.itmo.p3114.s312198.util.PersonBuilder;
import ru.itmo.p3114.s312198.util.StudyGroupBuilder;
import ru.itmo.p3114.s312198.util.SynchronizedCollectionManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class DBICommandValidator {
    private final DBHelper dbHelper = new DBHelper("C:\\Users\\yaros\\Desktop\\ИТМО\\" +
            "Семестр 2\\Лабораторные\\Программирование\\lab6\\server\\src\\main\\resources\\db.properties");

    private StudyGroup convert(ArrayList<String> arguments, int shift) throws ValueOutOfBoundsException {
        Location location = null;
        Person admin = null;
        if (arguments.size() > (6 + shift)) {
            if (arguments.size() > (10 + shift)) {
                location = new LocationBuilder()
                        .addCoords(arguments.get(10 + shift))
                        .addName(arguments.get(11 + shift))
                        .toLocation();
            }
            admin = new PersonBuilder()
                    .addName(arguments.get(6 + shift))
                    .addHeight(arguments.get(7 + shift))
                    .addHairColor(arguments.get(8 + shift))
                    .addNationality(arguments.get(9 + shift))
                    .addLocation(location)
                    .toPerson();
        }
        return new StudyGroupBuilder()
                .addName(arguments.get(shift))
                .addCoordinates(arguments.get(1 + shift))
                .addStudentsCount(arguments.get(2 + shift))
                .addShouldBeExpelled(arguments.get(3 + shift))
                .addTransferredStudents(arguments.get(4 + shift))
                .addFormOfEducation(arguments.get(5 + shift))
                .addGroupAdmin(admin)
                .toStudyGroup();
    }

    public boolean validate(AbstractCommand command, long creator, SynchronizedCollectionManager collectionManager) {
        try {
            StudyGroup studyGroup;
            switch (Commands.valueOf(command.getCommand().toUpperCase(Locale.ROOT))) {
                case ADD:
                    studyGroup = convert(command.getArguments(), 0);
                    studyGroup.setCreationDate(LocalDate.now());
                    studyGroup.setCreator(creator);
                    return dbHelper.createStudyGroup(studyGroup).getId() != -1;
                case ADD_IF_MAX:
                    studyGroup = convert(command.getArguments(), 0);
                    studyGroup.setCreationDate(LocalDate.now());
                    studyGroup.setCreator(creator);
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (studyGroup.compareTo(sg) < 0) {
                            return true;
                        }
                    }
                    return dbHelper.createStudyGroup(studyGroup).getId() != -1;
                case EXECUTE_SCRIPT:
                    CommandLineProcessor commandLineProcessor = new CommandLineProcessor();
                    collectionManager.execute(commandLineProcessor.parseFileInput(command.getArguments(), 0), creator);
                    return true;
                case REMOVE_ALL_BY_SHOULD_BE_EXPELLED:
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (sg.getShouldBeExpelled() == FieldParser.parseShouldBeExpelled(command.getArguments().get(0))) {
                            dbHelper.deleteStudyGroupById(sg.getId(), creator);
                        }
                    }
                    return true;
                case REMOVE_BY_ID:
                    return dbHelper.deleteStudyGroupById(Long.parseLong(command.getArguments().get(0)), creator);
                case REMOVE_ANY_BY_TRANSFERRED_STUDENTS:
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (sg.getTransferredStudents() == FieldParser.parseTransferredStudents(command.getArguments().get(0))) {
                            dbHelper.deleteStudyGroupById(sg.getId(), creator);
                            break;
                        }
                    }
                    return true;
                case REMOVE_GREATER:
                    studyGroup = convert(command.getArguments(), 0);
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (studyGroup.compareTo(sg) < 0) {
                            dbHelper.deleteStudyGroupById(sg.getId(), creator);
                        }
                    }
                    return true;
                case UPDATE:
                    studyGroup = convert(command.getArguments(), 1);
                    studyGroup.setCreationDate(LocalDate.now());
                    studyGroup.setCreator(creator);
                    studyGroup.setId(Long.parseLong(command.getArguments().get(0)));
                    return dbHelper.updateStudyGroup(studyGroup, creator);
                default:
                    return false;
            }
        } catch (ValueOutOfBoundsException ignored) {
            return false;
        }
    }

    public AuthorizationStatus authorize(AuthorizationRequest authorizationRequest, long accountId) {

        return AuthorizationStatus.ALLOWED;
    }
}
