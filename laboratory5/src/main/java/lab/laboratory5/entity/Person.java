package lab.laboratory5.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Person {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String passportID; //Значение этого поля должно быть уникальным, Поле не может быть null
    private lab.laboratory5.entity.colors.eye.Color eyeColor; //Поле может быть null
    private lab.laboratory5.entity.colors.hair.Color hairColor; //Поле не может быть null
    private Country nationality; //Поле может быть null

    public Person(String name, String passportID, lab.laboratory5.entity.colors.eye.Color eyeColor, lab.laboratory5.entity.colors.hair.Color hairColor, Country nationality) {
        this.name = name;
        this.passportID = passportID;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.nationality = nationality;
    }
}
