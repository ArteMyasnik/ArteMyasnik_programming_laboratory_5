package lab.laboratory5.commands.utils;

import lab.laboratory5.commands.done.PassportValidator;
import lab.laboratory5.entity.Country;
import lab.laboratory5.entity.Person;

public class PersonBuilder {
    private String name;
    private String passportID;
    private lab.laboratory5.entity.colors.eye.Color eyeColor;
    private lab.laboratory5.entity.colors.hair.Color hairColor;
    private Country nationality;

    public PersonBuilder name(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
        return this;
    }

    public PersonBuilder passportID(PassportValidator passportValidator, String passportID) {
        if (passportID == null) {
            throw new IllegalArgumentException("PassportID cannot be null");
        }
        this.passportID = passportID;
        return this;
    }

    public PersonBuilder eyeColor(lab.laboratory5.entity.colors.eye.Color eyeColor) {
        this.eyeColor = eyeColor;
        return this;
    }

    public PersonBuilder hairColor(lab.laboratory5.entity.colors.hair.Color hairColor) {
        if (hairColor == null) {
            throw new IllegalArgumentException("Hair color cannot be null");
        }
        this.hairColor = hairColor;
        return this;
    }

    public PersonBuilder nationality(Country nationality) {
        this.nationality = nationality;
        return this;
    }

    public Person build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Name cannot be null or empty");
        }
        if (passportID == null) {
            throw new IllegalStateException("PassportID cannot be null");
        }
        if (hairColor == null) {
            throw new IllegalStateException("Hair color cannot be null");
        }
        return new Person(name, passportID, eyeColor, hairColor, nationality);
    }
}