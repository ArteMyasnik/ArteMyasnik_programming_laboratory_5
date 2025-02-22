package lab.laboratory5.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Coordinates {
    private Double x; //Поле не может быть null
    private long y; //Значение поля должно быть больше -365

    public Coordinates(Double x, long y) {
        this.x = x;
        this.y = y;
    }
}
