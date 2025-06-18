package com.artemyasnik.db.dao;

import com.artemyasnik.collection.classes.Person;
import com.artemyasnik.db.query.PersonQuery;

import java.sql.*;

public class PersonDAO {
    public static void update(Connection connection, Person person, int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(PersonQuery.UPDATE_BY_ID.getQuery())) {
            mapPersonToPreparedStatement(person, preparedStatement);
            preparedStatement.setInt(6, id);
            preparedStatement.executeUpdate();
        }
    }

    public static Integer save(Connection connection, Person person) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(PersonQuery.SAVE.getQuery())) {
            mapPersonToPreparedStatement(person, preparedStatement);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) { return resultSet.getInt(1); } else { throw new SQLException("Creating Person failed, no id obtained"); }
            }
        }
    }

    public static void removeById(Connection connection, int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(PersonQuery.REMOVE_BY_ID.getQuery())) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private static void mapPersonToPreparedStatement(Person person, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(1, person.getName());
        preparedStatement.setString(2, person.getPassportID());
        preparedStatement.setString(3, String.valueOf(person.getHairColor()));
        if (person.getEyeColor() != null) { preparedStatement.setString(4, String.valueOf(person.getEyeColor())); } else { preparedStatement.setNull(4, Types.VARCHAR); }
        if (person.getNationality() != null) { preparedStatement.setString(5, String.valueOf(person.getNationality())); } else { preparedStatement.setNull(5, Types.VARCHAR); }

    }

    public static Person mapResultSetToPerson(ResultSet resultSet) throws SQLException {
        return new Person(resultSet.getString("name"),
                resultSet.getString("passport_id"),
                com.artemyasnik.collection.classes.colors.hair.Color.valueOf(resultSet.getString("hair_color")),
                resultSet.getString("eye_color") != null ? com.artemyasnik.collection.classes.colors.eyes.Color.valueOf(resultSet.getString("eye_color")) : null,
                resultSet.getString("nationality") != null ? com.artemyasnik.collection.classes.Country.valueOf(resultSet.getString("nationality")) : null);
    }
}