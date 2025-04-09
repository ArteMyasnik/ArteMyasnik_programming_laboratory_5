package com.artemyasnik.collection.classes;

import com.artemyasnik.collection.id.IdGenerator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@JacksonXmlRootElement(localName = "StudyGroup")
public class StudyGroup implements Comparable<StudyGroup> {
    @JacksonXmlProperty(isAttribute = true)
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    @JacksonXmlProperty(localName = "name")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @JacksonXmlProperty(localName = "coordinates")
    private Coordinates coordinates; //Поле не может быть null
    @JacksonXmlProperty(localName = "creationDate")
    private final ZonedDateTime creationDate = ZonedDateTime.now(); //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    @JacksonXmlProperty(localName = "studentsCount")
    private Integer studentsCount; //Значение поля должно быть больше 0, Поле может быть null
    @JacksonXmlProperty(localName = "transferredStudents")
    private int transferredStudents; //Значение поля должно быть больше 0
    @JacksonXmlProperty(localName = "semesterEnum")
    private Semester semesterEnum; //Поле не может быть null
    @Setter
    @JacksonXmlProperty(localName = "groupAdmin")
    private Person groupAdmin; //Поле не может быть null
    @Setter
    @JacksonXmlProperty(localName = "formOfEducation")
    private FormOfEducation formOfEducation; //Поле может быть null

    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("Name can't be null");
        if (name.isEmpty()) throw new IllegalArgumentException("Name can't be empty");
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) throw new IllegalArgumentException("Coordinates can't be null");
        this.coordinates = coordinates;
    }

    public void setStudentsCount(Integer studentsCount) {
        if (studentsCount != null && studentsCount <= 0) throw new IllegalArgumentException("Student count must be greater than zero");
        this.studentsCount = studentsCount;
    }

    public void setTransferredStudents(int transferredStudents) {
        if (transferredStudents <= 0) throw new IllegalArgumentException("Transferred students must be greater than zero");
        this.transferredStudents = transferredStudents;
    }

    public void setSemesterEnum(Semester semesterEnum) {
        if (semesterEnum == null) throw new IllegalArgumentException("Semester can't be null");
        this.semesterEnum = semesterEnum;
    }

    {
        this.id = IdGenerator.getInstance().generateId();
    }

    public void setId(Integer id) {
        if (id == null) throw new IllegalArgumentException("Id can't be null");
        if (id <= 0) throw new IllegalArgumentException("Id must be greater than zero");
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroup studyGroup = (StudyGroup) o;
        return transferredStudents == studyGroup.transferredStudents && Objects.equals(id, studyGroup.id) && Objects.equals(name, studyGroup.name) && Objects.equals(coordinates, studyGroup.coordinates) && Objects.equals(creationDate, studyGroup.creationDate) && Objects.equals(studentsCount, studyGroup.studentsCount) && semesterEnum == studyGroup.semesterEnum && Objects.equals(groupAdmin, studyGroup.groupAdmin) && formOfEducation == studyGroup.formOfEducation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, studentsCount, transferredStudents, semesterEnum, groupAdmin, formOfEducation);
    }

    @Override
    public int compareTo(StudyGroup o) {
        return CharSequence.compare(this.getName().toLowerCase(), o.getName().toLowerCase());
    }

    @Override
    public String toString() {
        return "StudyGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", studentsCount=" + studentsCount +
                ", transferredStudents=" + transferredStudents +
                ", semesterEnum=" + semesterEnum +
                ", groupAdmin=" + groupAdmin +
                ", formOfEducation=" + formOfEducation +
                '}';
    }
}