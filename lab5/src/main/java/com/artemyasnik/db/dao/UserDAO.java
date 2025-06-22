package com.artemyasnik.db.dao;

import com.artemyasnik.collection.passport.PassportValidator;
import com.artemyasnik.db.ConnectionFactory;
import com.artemyasnik.db.dto.UserDTO;
import com.artemyasnik.db.query.UserQuery;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserDAO {
//
//    public List<UserDTO> findAll() throws SQLException {
//        try (Connection connection = ConnectionFactory.getInstance().getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(UserQuery.FIND_ALL.getQuery());
//             ResultSet resultSet = preparedStatement.executeQuery()) {
//            return mapResultSetToList(resultSet);
//        }
//    }

    private static UserDAO INSTANCE;

    public static UserDAO getINSTANCE() {
        return INSTANCE == null ? INSTANCE = new UserDAO() : INSTANCE;
    }

    public Optional<UserDTO> findById(int id) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UserQuery.FIND_BY_ID.getQuery())) {

            preparedStatement.setInt(1, id);
            return executeQueryForSingleResult(preparedStatement);
        }
    }

    public Optional<UserDTO> findByUsername(String username) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UserQuery.FIND_BY_USERNAME.getQuery())) {
            preparedStatement.setString(1, username);
            return executeQueryForSingleResult(preparedStatement);
        }
    }

    public UserDTO create(UserDTO userDTO) throws SQLException {
        Objects.requireNonNull(userDTO, "User cannot be null");
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(UserQuery.SAVE.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, userDTO.username());
            preparedStatement.setString(2, userDTO.passwordHash());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) { throw new SQLException("Creating user failed, no rows affected."); }
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) { return new UserDTO(resultSet.getInt(1), userDTO.username(), userDTO.passwordHash()); }
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }

    public boolean existsByUsername(String username) throws SQLException {
        try (Connection connection = ConnectionFactory.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UserQuery.EXISTS_BY_USERNAME.getQuery())) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    public UserDTO registerUser(String username, String password) throws SQLException {
        if (existsByUsername(username)) { throw new IllegalArgumentException("Username already exists"); }
        return create(UserDTO.register(username, password));
    }

    public Optional<UserDTO> verify(String username, String password) throws SQLException {
        Optional<UserDTO> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) { return Optional.empty(); }
        return userOpt.get().verify(password) ? userOpt : Optional.empty();
    }

    private Optional<UserDTO> executeQueryForSingleResult(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return resultSet.next() ? Optional.of(mapResultSetToUserDTO(resultSet)) : Optional.empty();
        }
    }

    private List<UserDTO> mapResultSetToList(ResultSet resultSet) throws SQLException {
        List<UserDTO> users = new LinkedList<>();
        while (resultSet.next()) {
            users.add(mapResultSetToUserDTO(resultSet));
        }
        return users;
    }

    private UserDTO mapResultSetToUserDTO(ResultSet resultSet) throws SQLException {
        return new UserDTO(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("password")
        );
    }
}