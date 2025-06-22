package com.artemyasnik.db.dao;

import com.artemyasnik.collection.classes.Coordinates;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.db.ConnectionFactory;
import com.artemyasnik.db.query.StudyGroupQuery;

import java.sql.*;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public final class StudyGroupDAO {
    private static StudyGroupDAO INSTANCE;

    private StudyGroupDAO() {}

    public static StudyGroupDAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StudyGroupDAO();
        }
        return INSTANCE;
    }

    public List<StudyGroup> findAllWithPerson() throws SQLException {
        List<StudyGroup> groups = new LinkedList<>();
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.FIND_ALL_WITH_PERSON.getQuery());
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                groups.add(mapResultSetToStudyGroup(resultSet));
            }
        }
        return groups;
    }

    public List<StudyGroup> findAllWithPersonByOwnerId(int ownerId) throws SQLException {
        List<StudyGroup> groups = new LinkedList<>();
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.FIND_ALL_WITH_PERSON_BY_OWNER_ID.getQuery())) {
            preparedStatement.setInt(1, ownerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    groups.add(mapResultSetToStudyGroup(resultSet));
                }
            }
        }
        return groups;
    }

    public int saveAndReturnId(StudyGroup studyGroup, int ownerId) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    StudyGroupQuery.SAVE.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
                mapStudyGroupToPreparedStatement(studyGroup, ownerId, preparedStatement);
                if (studyGroup.getGroupAdmin() == null) {
                    preparedStatement.setNull(9, Types.INTEGER);
                } else {
                    int adminId = PersonDAO.getInstance().saveAndReturnId(connection, studyGroup.getGroupAdmin());
                    preparedStatement.setInt(9, adminId);
                }
                preparedStatement.executeUpdate();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        connection.commit();
                        return generatedId;
                    } else {
                        connection.rollback();
                        throw new SQLException("Creating study group failed, no ID obtained");
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void updateInDatabase(StudyGroup studyGroup, int ownerId) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!isOwner(connection, studyGroup.getId(), ownerId)) { throw new IllegalArgumentException("User is not the owner of this study group"); }
                Integer currentAdminId = getCurrentStudyGroupGroupAdminId(connection, studyGroup.getId());
                Integer newAdminId = null;

                if (studyGroup.getGroupAdmin() != null) {
                    if (currentAdminId != null) {
                        PersonDAO.getInstance().update(connection, studyGroup.getGroupAdmin(), currentAdminId);
                        newAdminId = currentAdminId;
                    } else { newAdminId = PersonDAO.getInstance().saveAndReturnId(connection, studyGroup.getGroupAdmin()); }
                } else if (currentAdminId != null) { PersonDAO.getInstance().removeById(connection, currentAdminId); }

                try (PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.UPDATE_BY_ID.getQuery())) {
                    mapStudyGroupToPreparedStatement(studyGroup, ownerId, preparedStatement);
                    preparedStatement.setObject(9, newAdminId, Types.INTEGER);
                    preparedStatement.setInt(10, studyGroup.getId());

                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Updating study group failed, no rows affected");
                    }
                    connection.commit();
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public void removeFromDatabase(int studyGroupId, int ownerId) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!isOwner(connection, studyGroupId, ownerId)) {
                    throw new IllegalArgumentException("User is not the owner of this study group");
                }

                Integer groupAdminId = getCurrentStudyGroupGroupAdminId(connection, studyGroupId);
                if (groupAdminId != null) {
                    PersonDAO.getInstance().removeById(connection, groupAdminId);
                }

                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        StudyGroupQuery.REMOVE_BY_ID.getQuery())) {
                    preparedStatement.setInt(1, studyGroupId);
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Deleting study group failed, no rows affected");
                    }
                    connection.commit();
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public boolean isOwner(int studyGroupId, int ownerId) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.FIND_OWNER_BY_ID.getQuery())) {
            preparedStatement.setInt(1, studyGroupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt("owner_id") == ownerId;
            }
        }
    }

    private boolean isOwner(Connection connection, int study_group_id, int owner_id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.FIND_OWNER_BY_ID.getQuery())) {
            preparedStatement.setInt(1, study_group_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt("owner_id") == owner_id;
            }
        }
    }

    private Integer getCurrentStudyGroupGroupAdminId(Connection connection, int study_group_id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.FIND_GROUP_ADMIN_BY_ID.getQuery())) {
            preparedStatement.setInt(1, study_group_id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getObject("group_admin_id", Integer.class) : null;
            }
        }
    }

    private void mapStudyGroupToPreparedStatement(StudyGroup studyGroup, int owner_id, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, owner_id);
        preparedStatement.setString(2, studyGroup.getName());
        preparedStatement.setDouble(3, studyGroup.getCoordinates().getX());
        preparedStatement.setLong(4, studyGroup.getCoordinates().getY());
        if (studyGroup.getStudentsCount() != null) { preparedStatement.setInt(5, studyGroup.getStudentsCount()); } else { preparedStatement.setNull(5, Types.INTEGER); }
        preparedStatement.setInt(6, studyGroup.getTransferredStudents());
        preparedStatement.setString(7, String.valueOf(studyGroup.getSemesterEnum()));
        if (studyGroup.getFormOfEducation() != null) { preparedStatement.setString(8, String.valueOf(studyGroup.getFormOfEducation())); } else { preparedStatement.setNull(8, Types.VARCHAR); }
    }

    public StudyGroup mapResultSetToStudyGroup(ResultSet resultSet) throws SQLException {
        return new StudyGroup(
                resultSet.getInt("study_group_id"),
                resultSet.getString("study_group_name"),
                new Coordinates(resultSet.getDouble("coordinate_x"), resultSet.getLong("coordinate_y")),
                resultSet.getObject("students_count", Integer.class),
                resultSet.getInt("transferred_students"),
                com.artemyasnik.collection.classes.Semester.valueOf(resultSet.getString("semester_enum")),
                com.artemyasnik.collection.classes.FormOfEducation.valueOf(resultSet.getString("form_of_education") != null ? resultSet.getString("form_of_education") : null),
                resultSet.getTimestamp("creation_date") != null ? resultSet.getTimestamp("creation_date").toInstant().atZone(ZoneId.systemDefault()) : null,
                PersonDAO.getInstance().mapResultSetToPerson(resultSet)
        );
    }
}