package com.artemyasnik.db.dao;


import com.artemyasnik.collection.classes.Coordinates;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.db.ConnectionFactory;
import com.artemyasnik.db.query.PersonQuery;
import com.artemyasnik.db.query.StudyGroupQuery;

import java.sql.*;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;

public class StudyGroupDAO {
    public List<StudyGroup> findAllWithPersonByOwnerId(int id) throws SQLException {
        List<StudyGroup> groups = new LinkedList<>();
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.FIND_ALL_WITH_PERSON_BY_OWNER_ID.getQuery())) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeQuery();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    groups.add(mapResultSetToStudyGroup(resultSet));
                }
            }
        }
        return groups;
    }

    public List<StudyGroup> findAllWithPerson() throws SQLException {
        List<StudyGroup> groups = new LinkedList<>();
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.FIND_ALL_WITH_PERSON.getQuery())) {
            preparedStatement.executeQuery();
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    groups.add(mapResultSetToStudyGroup(resultSet));
                }
            }
        }
        return groups;
    }

    public void save(StudyGroup studyGroup, int owner_id) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()){
            connection.setAutoCommit(false);
            try (PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.SAVE.getQuery())) {
                if (studyGroup.getGroupAdmin() == null) {
                    preparedStatement.setNull(9, Types.INTEGER);
                } else {
                    Integer group_admin_id = PersonDAO.save(connection, studyGroup.getGroupAdmin());
                    preparedStatement.setInt(9, group_admin_id);
                }
                mapStudyGroupToPreparedStatement(studyGroup, owner_id, preparedStatement);
                preparedStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Failed to save study group", e);
            }
        }
    }

    public void updateById(StudyGroup studyGroup, int owner_id, int study_group_id) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!isOwner(connection, study_group_id, owner_id)) {
                    throw new IllegalArgumentException("User is not the owner of this study group");
                }
                Integer currentAdminId = getCurrentStudyGroupGroupAdminId(connection, study_group_id);
                Integer newAdminId = null;
                if (studyGroup.getGroupAdmin() != null) {
                    if (currentAdminId != null) {
                        PersonDAO.update(connection, studyGroup.getGroupAdmin(), currentAdminId);
                        newAdminId = currentAdminId;
                    } else { newAdminId = PersonDAO.save(connection, studyGroup.getGroupAdmin()); }
                } else {
                    if (currentAdminId != null) { PersonDAO.removeById(connection, currentAdminId); }
                    newAdminId = null;
                }
                try (PreparedStatement preparedStatement = connection.prepareStatement(StudyGroupQuery.UPDATE_BY_ID.getQuery())) {
                    mapStudyGroupToPreparedStatement(studyGroup, owner_id, preparedStatement);
                    preparedStatement.setObject(9, newAdminId, Types.INTEGER);
                    preparedStatement.setInt(10, study_group_id);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Failed to update study group", e);
            }
        }
    }

    public void removeById(int owner_id, int study_group_id) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!isOwner(connection, study_group_id, owner_id)) {
                    throw new IllegalArgumentException("User is not the owner of this study group");
                }
                Integer adminId = getCurrentStudyGroupGroupAdminId(connection, study_group_id);
                try (PreparedStatement preparedStatement = connection.prepareStatement(PersonQuery.REMOVE_BY_ID.getQuery())) {
                    preparedStatement.setInt(1, study_group_id);
                    preparedStatement.executeUpdate();
                }
                if (adminId != null) { PersonDAO.removeById(connection, adminId); }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new SQLException("Failed to remove study group", e);
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
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next() ? rs.getObject("group_admin_id", Integer.class) : null;
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
                PersonDAO.mapResultSetToPerson(resultSet)
        );
    }
}