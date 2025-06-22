package com.artemyasnik.db.dto;


import com.artemyasnik.db.util.PasswordUtil;

import java.io.Serializable;
import java.util.Objects;

public record UserDTO(Integer id, String username, String passwordHash) implements Serializable {
    public UserDTO {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(passwordHash, "Password hash cannot be null");
    }

    public static UserDTO register(String username, String plainPassword) {
        if (username == null || username.isBlank()) { throw new IllegalArgumentException("Username cannot be empty"); }
        if (plainPassword == null || plainPassword.isBlank()) { throw new IllegalArgumentException("Password cannot be empty"); }
        return new UserDTO(null, username, PasswordUtil.hash(plainPassword));
    }

    public boolean verify(String plainPassword) { return PasswordUtil.verify(plainPassword, this.passwordHash); }
}