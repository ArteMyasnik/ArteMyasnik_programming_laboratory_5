package com.artemyasnik.db.query;

import lombok.Getter;

@Getter
public enum PersonQuery {
    REMOVE_BY_ID("DELETE FROM persons WHERE id = ?"),
    SAVE("INSERT INTO persons (name, passport_id, hair_color, eye_color, nationality) VALUES (?, ?, ?, ?, ?)"),
    UPDATE_BY_ID("UPDATE persons SET name = ?, passport_id = ?, hair_color = ? eye_color = ?, nationality = ? WHERE id = ?");

    private final String query;

    PersonQuery(String query) {
        this.query = query;
    }
}
