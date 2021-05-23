package ru.itmo.p3114.s312198.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.p3114.s312198.collection.Color;
import ru.itmo.p3114.s312198.collection.Coordinates;
import ru.itmo.p3114.s312198.collection.Country;
import ru.itmo.p3114.s312198.collection.Location;
import ru.itmo.p3114.s312198.collection.Person;
import ru.itmo.p3114.s312198.collection.StudyGroup;
import ru.itmo.p3114.s312198.util.StudyGroupBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class DBHelper {
    static final Logger logger = LoggerFactory.getLogger(DBHelper.class);

    private HikariConfig config;
    private HikariDataSource dataSource;

    public DBHelper(String pathToConfig) {
        config = new HikariConfig(pathToConfig);
        dataSource = new HikariDataSource(config);
    }

    public void closeDataSource() {
        if ((dataSource != null) && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public Person getPersonById(Long id) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select " +
                    "name, height, color, nationality, " +
                    "location_x, location_y, location_z, location_name from persons where id = ?")) {
                preparedStatement.setLong(1, id);
                preparedStatement.execute();
                ResultSet resultSet = preparedStatement.getResultSet();
                Person person = new Person();
                person.setName(resultSet.getString(1));
                person.setHeight(resultSet.getLong(2));
                person.setHairColor(Color.colorByID(resultSet.getInt(3)));
                person.setNationality(Country.countryByID(resultSet.getInt(4)));
                person.setLocation(new Location(resultSet.getInt(5), resultSet.getInt(6), resultSet.getInt(7),
                        resultSet.getString(8)));

                resultSet.close();
                return person;
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        }
        return null;
    }

    public Boolean credentialsValid(String name, String credentials) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select count(*) as num from accounts where login = ? and credentials = ?")) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, credentials);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.getInt(1) == 1;
                }
            }
        }
    }

    public void createCredentials(String name, String credentials) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into accounts(id, login, credentials)\n" +
                    "values (nextval('seq_accounts'), ?, ?)")) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, credentials);
                preparedStatement.execute();
            }
        }
    }

    public LinkedHashSet<StudyGroup> getStudyGroups() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("select id, name, coord_x, coord_y, created, " +
                    "creator, students_count, \n" +
                    "should_be_expelled, transferred_students, \n" +
                    "form_of_education, group_admin from study_groups")) {
                LinkedHashSet<StudyGroup> studyGroups = new LinkedHashSet<>();
                if (preparedStatement.execute()) {
                    ResultSet resultSet = preparedStatement.getResultSet();
                    while (resultSet.next()) {
                        studyGroups.add(new StudyGroupBuilder()
                                .addName(resultSet.getString(2))
                                .addCoordinates(new Coordinates(resultSet.getInt(3), resultSet.getDouble(4)))
                                .addGroupAdmin(getPersonById(resultSet.getLong(11))).toStudyGroup());
                    }
                }
                return studyGroups;
            }
        }
    }
}
