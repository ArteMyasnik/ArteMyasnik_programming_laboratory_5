package lab.laboratory5.commands.utils;

import lab.laboratory5.entity.*;
import java.time.ZonedDateTime;

public class StudyGroupBuilder {
    private static int lastGeneratedId = 0;
    private Integer id;
    private String name;
    private Coordinates coordinates;
    private ZonedDateTime creationDate;
    private Integer studentsCount;
    private int transferredStudents;
    private FormOfEducation formOfEducation;
    private Semester semesterEnum;
    private Person groupAdmin;

    public StudyGroupBuilder() {
        this.id = generateId();
        this.creationDate = ZonedDateTime.now();
    }

    public StudyGroupBuilder semesterEnum(Semester semesterEnum) {
        if (semesterEnum == null) {
            throw new IllegalArgumentException("Semester cannot be null");
        }
        this.semesterEnum = semesterEnum;
        return this;
    }

    public StudyGroupBuilder groupAdmin(Person groupAdmin) {
        if (groupAdmin == null) {
            throw new IllegalArgumentException("Group admin cannot be null");
        }
        this.groupAdmin = groupAdmin;
        return this;
    }

    public StudyGroupBuilder transferredStudents(int transferredStudents) {
        if (transferredStudents <= 0) {
            throw new IllegalArgumentException("Transferred students must be greater than 0");
        }
        this.transferredStudents = transferredStudents;
        return this;
    }

    public StudyGroupBuilder name(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        return this;
    }

    public StudyGroupBuilder coordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        this.coordinates = coordinates;
        return this;
    }

    public StudyGroupBuilder studentsCount(Integer studentsCount) {
        if (studentsCount != null && studentsCount <= 0) {
            throw new IllegalArgumentException("Students count must be greater than 0");
        }
        this.studentsCount = studentsCount;
        return this;
    }

    public StudyGroupBuilder formOfEducation(FormOfEducation formOfEducation) {
        this.formOfEducation = formOfEducation;
        return this;
    }

    public StudyGroup build() {
        return new StudyGroup(id, name, coordinates, creationDate, studentsCount, transferredStudents, formOfEducation, semesterEnum, groupAdmin);
    }

    private int generateId() {
        lastGeneratedId++;
        return lastGeneratedId;
    }
}