package com.artemyasnik.collection.classes;

import com.artemyasnik.collection.classes.colors.hair.Color;
import com.artemyasnik.collection.passport.PassportValidator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@JacksonXmlRootElement(localName = "Person")
public class Person implements Serializable {
//    @Setter
//    @JsonIgnore
//    private Integer id; // Поле id необходимое для связи объектов в базе данных
    @JacksonXmlProperty(localName = "name")
    private String name; //Поле не может быть null, Строка не может быть пустой
    @JacksonXmlProperty(localName = "passportID")
    private String passportID; //Значение этого поля должно быть уникальным, Поле не может быть null
    @JacksonXmlProperty(localName = "hairColor")
    private com.artemyasnik.collection.classes.colors.hair.Color hairColor; //Поле не может быть null
    @Setter
    @JacksonXmlProperty(localName = "eyeColor")
    private com.artemyasnik.collection.classes.colors.eyes.Color eyeColor; //Поле может быть null
    @Setter
    @JacksonXmlProperty(localName = "nationality")
    private Country nationality; //Поле может быть null

    public Person() {}

    public Person(String name, String passportID, Color hairColor, com.artemyasnik.collection.classes.colors.eyes.Color eyeColor, Country nationality) {
        this.name = name;
        this.passportID = passportID;
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("Name can't be null");
        if (name.isEmpty()) throw new IllegalArgumentException("Name can't be empty");
        this.name = name;
    }

    public void setHairColor(com.artemyasnik.collection.classes.colors.hair.Color color) {
        if (color == null) throw new IllegalArgumentException("Color can't be null");
        this.hairColor = color;
    }

    public void setPassportID(String passportID) {
        if (passportID == null) throw new IllegalArgumentException("PassportID can't be null");
        try {
            if (PassportValidator.getInstance().validate(passportID)) this.passportID = passportID;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(getName(), person.getName()) && Objects.equals(getPassportID(), person.getPassportID()) && getHairColor() == person.getHairColor() && getEyeColor() == person.getEyeColor() && getNationality() == person.getNationality();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPassportID(), getHairColor(), getEyeColor(), getNationality());
    }

    @Override
    public String toString() {
        if (this == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder("Person{");
        boolean hasContent = false;

        if (name != null) {
            sb.append("name='").append(name).append('\'');
            hasContent = true;
        }

        if (passportID != null) {
            if (hasContent) sb.append(", ");
            sb.append("passportID='").append(passportID).append('\'');
            hasContent = true;
        }

        if (hairColor != null) {
            if (hasContent) sb.append(", ");
            sb.append("hairColor=").append(hairColor);
            hasContent = true;
        }

        if (eyeColor != null) {
            if (hasContent) sb.append(", ");
            sb.append("eyeColor=").append(eyeColor);
            hasContent = true;
        }

        if (nationality != null) {
            if (hasContent) sb.append(", ");
            sb.append("nationality=").append(nationality);
        }

        sb.append('}');
        return sb.toString();
    }
}