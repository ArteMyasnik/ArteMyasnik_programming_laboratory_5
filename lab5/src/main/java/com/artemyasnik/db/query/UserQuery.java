package com.artemyasnik.db.query;

import lombok.Getter;

@Getter
public enum UserQuery {
    FIND_ALL("SELECT * FROM clients"),
    FIND_BY_USERNAME("SELECT * FROM clients WHERE username = ?"),
    FIND_BY_ID("SELECT * FROM clients WHERE id = ?"),
    SAVE("INSERT INTO clients (username, password) VALUES (?, ?)"),
    EXISTS_BY_USERNAME("SELECT COUNT(*) FROM clients WHERE username = ?");

    private final String query;

    UserQuery(String query) {
        this.query = query;
    }

}
