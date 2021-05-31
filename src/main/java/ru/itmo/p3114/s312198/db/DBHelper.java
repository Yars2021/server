package ru.itmo.p3114.s312198.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.collection.Color;
import ru.itmo.p3114.s312198.collection.Coordinates;
import ru.itmo.p3114.s312198.collection.Country;
import ru.itmo.p3114.s312198.collection.FormOfEducation;
import ru.itmo.p3114.s312198.collection.Location;
import ru.itmo.p3114.s312198.collection.Person;
import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.exception.InvalidPathException;
import ru.itmo.p3114.s312198.util.PersonBuilder;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashSet;

public class DBHelper {
    static final Logger logger = LoggerFactory.getLogger(DBHelper.class);

    private final HikariDataSource dataSource;

    public DBHelper() throws InvalidPathException {
        String resources = getConfigFromResources();
        if (resources == null) {
            throw new InvalidPathException();
        }
        HikariConfig config = new HikariConfig(resources);
        dataSource = new HikariDataSource(config);
    }

    private String getConfigFromResources() {
        String pathToFile = null;
        try {
            URL fileURL = this.getClass().getClassLoader().getResource("db.properties");
            if (fileURL != null) {
                pathToFile = URLDecoder.decode(fileURL.getPath(), StandardCharsets.UTF_8.name());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return pathToFile;
    }

    public void closeDataSource() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public Account createAccount(Account account) throws SQLException {
        Account createdAccount = new Account();
        if (account == null) {
            createdAccount.setId(null);
        } else {
            createdAccount.setLogin(account.getLogin());
            createdAccount.setCredentials(account.getCredentials());
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "insert into accounts (id, login, credentials) values (nextval('seq_accounts'), ?, ?) returning id")) {
                    preparedStatement.setString(1, account.getLogin());
                    preparedStatement.setString(2, account.getCredentials());
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            createdAccount.setId(resultSet.getLong(1));
                        }
                    }
                }
            } catch (SQLException sqle) {
                logger.error(sqle.getMessage());
                throw sqle;
            }
        }
        return createdAccount;
    }

    public boolean credentialsValid(String login, String credentials) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select count(id)" +
                    "as num from accounts where login = ? and credentials = ?")) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, credentials);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) == 1;
                    }
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
        return false;
    }

    public Account getAccountByLoginAndCredentials(String login, String credentials) {
        Account account = new Account();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select id"
                    + "  from accounts where login = ? and credentials = ?")) {
                preparedStatement.setString(1, login);
                preparedStatement.setString(2, credentials);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        account.setId(resultSet.getLong(1));
                        account.setLogin(login);
                        account.setCredentials(credentials);
                    }
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
            return null;
        }
        return account;
    }

    public Person getPersonById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select " +
                    "name, height, color, nationality, " +
                    "location_x, location_y, location_z, location_name from persons where id = ?")) {
                preparedStatement.setLong(1, id);
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getResultSet();
                if (resultSet.next()) {
                    Person person = new PersonBuilder()
                            .addId(id)
                            .addName(resultSet.getString(1))
                            .addHeight(resultSet.getLong(2))
                            .addHairColor(Color.colorByID(resultSet.getInt(3)))
                            .addNationality(Country.countryByID(resultSet.getInt(4)))
                            .addLocation(new Location(resultSet.getFloat(5),
                                    resultSet.getFloat(6),
                                    resultSet.getFloat(7),
                                    resultSet.getString(8)))
                            .toPerson();
                    resultSet.close();
                    return person;
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
        return null;
    }

    public Person createPerson(Person person) {
        Person createdPerson = new Person();
        if (person == null) {
            createdPerson.setId(-1);
        } else {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("insert into persons " +
                        "(id, name, height, color, nationality, location_x, location_y, location_z, location_name) " +
                        "values (nextval('seq_person'), ?, ?, ?, ?, ?, ?, ?, ?) returning id")) {
                    preparedStatement.setString(1, person.getName());
                    preparedStatement.setLong(2, person.getHeight());
                    preparedStatement.setInt(3, person.getHairColor().getValue());
                    preparedStatement.setInt(4, person.getNationality().getValue());
                    if (person.getLocation() != null) {
                        preparedStatement.setFloat(5, person.getLocation().getX());
                        preparedStatement.setFloat(6, person.getLocation().getY());
                        preparedStatement.setFloat(7, person.getLocation().getZ());
                        preparedStatement.setString(8, person.getLocation().getName());
                    } else {
                        preparedStatement.setFloat(5, 0);
                        preparedStatement.setFloat(6, 0);
                        preparedStatement.setFloat(7, 0);
                        preparedStatement.setString(8, "");
                    }
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            createdPerson.setId(resultSet.getLong(1));
                            createdPerson.setName(person.getName());
                            createdPerson.setHeight(person.getHeight());
                            createdPerson.setHairColor(person.getHairColor());
                            createdPerson.setNationality(person.getNationality());
                            if (person.getLocation() != null) {
                                createdPerson.setLocation(new Location(person.getLocation().getX(),
                                        person.getLocation().getY(),
                                        person.getLocation().getZ(),
                                        person.getLocation().getName()));
                            }
                        }
                    }
                }
            } catch (SQLException sqle) {
                logger.error(sqle.getMessage());
                createdPerson.setId(-1);
            }
        }
        return createdPerson;
    }

    public StudyGroup getStudyGroupById(Long id) {
        StudyGroup studyGroup = new StudyGroup();
        studyGroup.setId(id);
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select id, name, coord_x, coord_y," +
                    "       created, creator," +
                    "       students_count, should_be_expelled, transferred_students," +
                    "       form_of_education, group_admin" +
                    "  from study_groups" +
                    " where id = ?")) {
                preparedStatement.setLong(1, id);
                if (preparedStatement.execute()) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    if (resultSet.next()) {
                        studyGroup.setName(resultSet.getString(2));
                        studyGroup.setCoordinates(new Coordinates(resultSet.getLong(3), resultSet.getDouble(4)));
                        studyGroup.setCreationDate(resultSet.getDate(5).toLocalDate());
                        studyGroup.setCreator(resultSet.getLong(6));
                        studyGroup.setStudentsCount(resultSet.getInt(7));
                        studyGroup.setShouldBeExpelled(resultSet.getInt(8));
                        studyGroup.setTransferredStudents(resultSet.getInt(9));
                        studyGroup.setFormOfEducation(FormOfEducation.formOfEducationByID(resultSet.getInt(10)));
                        if (resultSet.getLong(11) > 0) {
                            studyGroup.setGroupAdmin(getPersonById(resultSet.getLong(11)));
                        }
                    }
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
        return studyGroup;
    }

    public StudyGroup createStudyGroup(StudyGroup studyGroup) {
        StudyGroup createdStudyGroup = new StudyGroup();
        if (studyGroup == null) {
            createdStudyGroup.setId(-1);
        } else {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("insert into study_groups " +
                        "(id, name, coord_x, coord_y, creator, " +
                        "students_count, should_be_expelled, " +
                        "transferred_students, form_of_education, " +
                        "group_admin) " +
                        "values (nextval('seq_study_groups'), ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "returning id")) {
                    preparedStatement.setString(1, studyGroup.getName());
                    preparedStatement.setLong(2, studyGroup.getCoordinates().getX());
                    preparedStatement.setDouble(3, studyGroup.getCoordinates().getY());
                    preparedStatement.setLong(4, studyGroup.getCreator());
                    preparedStatement.setInt(5, studyGroup.getStudentsCount());
                    preparedStatement.setInt(6, studyGroup.getShouldBeExpelled());
                    preparedStatement.setInt(7, studyGroup.getTransferredStudents());
                    preparedStatement.setInt(8, studyGroup.getFormOfEducation().getValue());
                    if (studyGroup.getGroupAdmin() == null) {
                        preparedStatement.setNull(9, Types.INTEGER);
                    } else {
                        studyGroup.setGroupAdmin(createPerson(studyGroup.getGroupAdmin()));
                        preparedStatement.setLong(9, studyGroup.getGroupAdmin().getId());
                    }
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            createdStudyGroup.setId(resultSet.getLong(1));
                            createdStudyGroup.setName(studyGroup.getName());
                            createdStudyGroup.setCoordinates(new Coordinates(studyGroup.getCoordinates().getX(), studyGroup.getCoordinates().getY()));
                            createdStudyGroup.setCreationDate(studyGroup.getCreationDate());
                            createdStudyGroup.setCreator(studyGroup.getCreator());
                            createdStudyGroup.setStudentsCount(studyGroup.getStudentsCount());
                            createdStudyGroup.setShouldBeExpelled(studyGroup.getShouldBeExpelled());
                            createdStudyGroup.setTransferredStudents(studyGroup.getTransferredStudents());
                            createdStudyGroup.setFormOfEducation(studyGroup.getFormOfEducation());
                            if (studyGroup.getGroupAdmin() != null) {
                                createdStudyGroup.setGroupAdmin(getPersonById(studyGroup.getGroupAdmin().getId()));
                            } else {
                                createdStudyGroup.setGroupAdmin(null);
                            }
                        }
                    }
                }
            } catch (SQLException sqle) {
                logger.error(sqle.getMessage());
                createdStudyGroup.setId(-1);
            }
        }
        return createdStudyGroup;
    }

    public boolean updateStudyGroup(StudyGroup studyGroup, Long accountId) {
        boolean updated = false;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("update study_groups " +
                    "    set name = ?," +
                    "        coord_x = ?," +
                    "        coord_y = ?," +
                    "        students_count = ?," +
                    "        should_be_expelled = ?," +
                    "        transferred_students = ?," +
                    "        form_of_education = ?," +
                    "        group_admin = ?" +
                    "  where id = ?" +
                    "    and creator = ?")) {
                preparedStatement.setString(1, studyGroup.getName());
                preparedStatement.setLong(2, studyGroup.getCoordinates().getX());
                preparedStatement.setDouble(3, studyGroup.getCoordinates().getY());
                preparedStatement.setInt(4, studyGroup.getStudentsCount());
                preparedStatement.setInt(5, studyGroup.getShouldBeExpelled());
                preparedStatement.setInt(6, studyGroup.getTransferredStudents());
                preparedStatement.setInt(7, studyGroup.getFormOfEducation().getValue());
                if (studyGroup.getGroupAdmin() == null) {
                    preparedStatement.setNull(8, Types.INTEGER);
                } else {
                    preparedStatement.setLong(8, studyGroup.getGroupAdmin().getId());
                }
                preparedStatement.setLong(9, studyGroup.getId());
                preparedStatement.setLong(10, accountId);
                if (preparedStatement.executeUpdate() == 1) {
                    updated = true;
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
        return updated;
    }

    public Boolean deleteStudyGroupById(Long id, Long accountId) {
        Boolean deleted = Boolean.FALSE;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from study_groups where id = ? and creator = ?")) {
                preparedStatement.setLong(1, id);
                preparedStatement.setLong(2, accountId);
                if (preparedStatement.executeUpdate() == 1) {
                    deleted = Boolean.TRUE;
                }
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
        return deleted;
    }

    public LinkedHashSet<StudyGroup> getStudyGroups() {
        LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select " +
                    "id, name, coord_x, coord_y, created, creator," +
                    "students_count, should_be_expelled," +
                    "transferred_students, form_of_education," +
                    "group_admin from studs.s312198.study_groups")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    StudyGroup studyGroup = new StudyGroup();
                    studyGroup.setId(resultSet.getLong(1));
                    studyGroup.setName(resultSet.getString(2));
                    studyGroup.setCoordinates(new Coordinates(resultSet.getLong(3), resultSet.getDouble(4)));
                    studyGroup.setCreationDate(resultSet.getDate(5).toLocalDate());
                    studyGroup.setCreator(resultSet.getLong(6));
                    studyGroup.setStudentsCount(resultSet.getInt(7));
                    studyGroup.setShouldBeExpelled(resultSet.getInt(8));
                    studyGroup.setTransferredStudents(resultSet.getInt(9));
                    studyGroup.setFormOfEducation(FormOfEducation.formOfEducationByID(resultSet.getInt(10)));
                    if (resultSet.getLong(11) > 0) {
                        studyGroup.setGroupAdmin(getPersonById(resultSet.getLong(11)));
                    }
                    studyGroups.add(studyGroup);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            logger.error(sqle.getMessage());
        }
        return studyGroups;
    }

    public void loadStudyGroups(LinkedHashSet<StudyGroup> studyGroups) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from study_groups")) {
                preparedStatement.execute();
            }
            for (StudyGroup studyGroup : studyGroups) {
                createStudyGroup(studyGroup);
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
    }
}