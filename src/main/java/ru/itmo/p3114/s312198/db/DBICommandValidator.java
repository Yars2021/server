package ru.itmo.p3114.s312198.db;

import ru.itmo.p3114.s312198.collection.Location;
import ru.itmo.p3114.s312198.collection.Person;
import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.command.CommandLineProcessor;
import ru.itmo.p3114.s312198.command.Commands;
import ru.itmo.p3114.s312198.command.actions.AbstractCommand;
import ru.itmo.p3114.s312198.exception.ValueOutOfBoundsException;
import ru.itmo.p3114.s312198.transmission.structures.authorization.AuthorizationRequest;
import ru.itmo.p3114.s312198.util.FieldParser;
import ru.itmo.p3114.s312198.util.LocationBuilder;
import ru.itmo.p3114.s312198.util.PersonBuilder;
import ru.itmo.p3114.s312198.util.StudyGroupBuilder;
import ru.itmo.p3114.s312198.util.SynchronizedCollectionManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class DBICommandValidator {
    private final DBHelper dbHelper = new DBHelper();

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

    public ValidationVerdict validate(AbstractCommand command, long creator, SynchronizedCollectionManager collectionManager) {
        try {
            long newId;
            StudyGroup studyGroup;
            switch (Commands.valueOf(command.getCommand().toUpperCase(Locale.ROOT))) {
                case ADD:
                    studyGroup = convert(command.getArguments(), 0);
                    studyGroup.setCreationDate(LocalDate.now());
                    studyGroup.setCreator(creator);
                    newId = dbHelper.createStudyGroup(studyGroup).getId();
                    return new ValidationVerdict(newId != -1, newId);
                case ADD_IF_MAX:
                    studyGroup = convert(command.getArguments(), 0);
                    studyGroup.setCreationDate(LocalDate.now());
                    studyGroup.setCreator(creator);
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (studyGroup.compareTo(sg) < 0) {
                            return new ValidationVerdict(false, -1);
                        }
                    }
                    newId = dbHelper.createStudyGroup(studyGroup).getId();
                    return new ValidationVerdict(newId != -1, newId);
                case EXECUTE_SCRIPT:
                    CommandLineProcessor commandLineProcessor = new CommandLineProcessor();
                    collectionManager.execute(commandLineProcessor.parseFileInput(command.getArguments(), 0), creator);
                    return new ValidationVerdict(true, -1);
                case REMOVE_ALL_BY_SHOULD_BE_EXPELLED:
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (sg.getShouldBeExpelled() == FieldParser.parseShouldBeExpelled(command.getArguments().get(0))) {
                            dbHelper.deleteStudyGroupById(sg.getId(), creator);
                        }
                    }
                    return new ValidationVerdict(true, -1);
                case REMOVE_BY_ID:
                    return new ValidationVerdict(dbHelper.deleteStudyGroupById(Long.parseLong(command.getArguments().get(0)), creator), -1);
                case REMOVE_ANY_BY_TRANSFERRED_STUDENTS:
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (sg.getTransferredStudents() == FieldParser.parseTransferredStudents(command.getArguments().get(0))) {
                            dbHelper.deleteStudyGroupById(sg.getId(), creator);
                            break;
                        }
                    }
                    return new ValidationVerdict(true, -1);
                case REMOVE_GREATER:
                    studyGroup = convert(command.getArguments(), 0);
                    for (StudyGroup sg : dbHelper.getStudyGroups()) {
                        if (studyGroup.compareTo(sg) < 0) {
                            dbHelper.deleteStudyGroupById(sg.getId(), creator);
                        }
                    }
                    new ValidationVerdict(true, -1);
                case UPDATE:
                    studyGroup = convert(command.getArguments(), 1);
                    studyGroup.setCreationDate(LocalDate.now());
                    studyGroup.setCreator(creator);
                    studyGroup.setId(Long.parseLong(command.getArguments().get(0)));
                    return new ValidationVerdict(dbHelper.updateStudyGroup(studyGroup, creator), -1);
                case CLEAR:
                    return new ValidationVerdict(true, -1);
                default:
                    return new ValidationVerdict(false, -1);
            }
        } catch (ValueOutOfBoundsException ignored) {
            return new ValidationVerdict(false, -1);
        }
    }

    public AccountData authorize(AuthorizationRequest authorizationRequest) {
        AuthorizationStatus authorizationStatus;
        Account account;
        long id = -1;
        switch (authorizationRequest.getType()) {
            case "LOG":
                account = dbHelper.getAccountByLoginAndCredentials(
                        authorizationRequest.getUserSignature().getUsername(), authorizationRequest.getUserSignature().getPassHash());
                if (account == null || account.getId() == null) {
                    authorizationStatus = AuthorizationStatus.INCORRECT_CREDENTIALS;
                } else {
                    id = account.getId();
                    authorizationStatus = AuthorizationStatus.ALLOWED;
                }
                break;
            case "REG":
                try {
                    account = dbHelper.createAccount(
                            new Account(-1L, authorizationRequest.getUserSignature().getUsername(), authorizationRequest.getUserSignature().getPassHash()));
                    if (account == null) {
                        authorizationStatus = AuthorizationStatus.UNDEFINED;
                    } else {
                        id = account.getId();
                        authorizationStatus = AuthorizationStatus.ALLOWED;
                    }
                } catch (SQLException sqle) {
                    authorizationStatus = AuthorizationStatus.USERNAME_IS_TAKEN;
                    return new AccountData(authorizationStatus, id);
                }
                break;
            default:
                authorizationStatus = AuthorizationStatus.BANNED;
        }
        return new AccountData(authorizationStatus, id);
    }
}
