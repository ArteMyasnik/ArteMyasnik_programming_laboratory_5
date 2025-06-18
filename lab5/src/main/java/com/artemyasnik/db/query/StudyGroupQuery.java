package com.artemyasnik.db.query;

import lombok.Getter;

@Getter
public enum StudyGroupQuery {
    FIND_ALL_WITH_PERSON("SELECT * FROM study_groups LEFT OUTER JOIN persons ON study_groups.group_admin_id = persons.id"),
    FIND_ALL_WITH_PERSON_BY_OWNER_ID("SELECT study_groups.id AS study_group_id, study_groups.name AS study_group_name, study_groups1.coordinate_x, study_groups.coordinate_y, study_groups.students_count, study_groups.transferred_students, study_groups.semester_enum, study_groups.form_of_education, study_groups.creation_date, study_groups.group_admin_id, persons.id AS person_id, persons.name, persons.passport_id, persons.hair_color, persons.eye_color, persons.nationality, clients.id, clients.username FROM study_groups INNER JOIN clients ON study_groups.owner_id = clients.id LEFT OUTER JOIN persons ON study_groups.group_admin_id = persons.id WHERE clients.id = ?"),
    FIND_GROUP_ADMIN_BY_ID("SELECT group_admin_id FROM study_groups WHERE id = ?"),
    FIND_OWNER_BY_ID("SELECT owner_id FROM study_groups WHERE id = ?"),
    SAVE("INSERT INTO study_groups (owner_id, name, coordinate_x, coordinate_y, students_count, transferred_students, semester_enum, form_of_education, group_admin_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"),
    REMOVE_BY_ID("DELETE FROM study_groups WHERE id = ?"),
    UPDATE_BY_ID("UPDATE study_groups SET owner_id = ?, name = ?, coordinate_x = ?, coordinate_y = ?, students_count = ?, transferred_students = ?, semester_enum = ?, form_of_education = ?, group_admin_id = ? WHERE id = ?;");

    private final String query;

    StudyGroupQuery(String query) {
        this.query = query;
    }

}
