package com.artemyasnik.collection.classes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@JacksonXmlRootElement(localName = "Coordinates")
public class Coordinates {
    @JacksonXmlProperty(localName = "x")
    private Double x; //Поле не может быть null
    @Setter
    @JacksonXmlProperty(localName = "y")
    private long y; //Значение поля должно быть больше -365

    public void setX(Double x) {
        if (x == null) throw new IllegalArgumentException("X can't be null");
        if (x < -365) throw new IllegalArgumentException("X must be greater than -365");
        this.x = x;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return y == that.y && Objects.equals(x, that.x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}