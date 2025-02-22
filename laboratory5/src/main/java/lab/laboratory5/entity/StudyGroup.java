package lab.laboratory5.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@ToString
public class StudyGroup {
    private final Integer id; // Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; // Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; // Поле не может быть null
    private final ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final Integer studentsCount; // Значение поля должно быть больше 0, Поле может быть null
    private final int transferredStudents; // Значение поля должно быть больше 0
    private final FormOfEducation formOfEducation; // Поле может быть null
    private final Semester semesterEnum; // Поле не может быть null
    private final Person groupAdmin; // Поле не может быть null

    public StudyGroup(Integer id, String name, Coordinates coordinates, ZonedDateTime creationDate, Integer studentsCount, int transferredStudents, FormOfEducation formOfEducation, Semester semesterEnum, Person groupAdmin) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.studentsCount = studentsCount;
        this.transferredStudents = transferredStudents;
        this.formOfEducation = formOfEducation;
        this.semesterEnum = semesterEnum;
        this.groupAdmin = groupAdmin;
    }
}