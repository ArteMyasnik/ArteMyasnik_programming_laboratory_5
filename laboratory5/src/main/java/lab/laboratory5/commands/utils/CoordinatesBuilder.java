package lab.laboratory5.commands.utils;

import lab.laboratory5.entity.Coordinates;

public class CoordinatesBuilder {
    private Double x;
    private long y;

    public CoordinatesBuilder x(Double x) {
        if (x == null) {
            throw new IllegalArgumentException("Coordinate x cannot be null");
        }
        this.x = x;
        return this;
    }

    public CoordinatesBuilder y(long y) {
        if (y <= -365) {
            throw new IllegalArgumentException("Coordinate y should be greater than -365");
        }
        this.y = y;
        return this;
    }

    public Coordinates build() {
        if (x == null) {
            throw new IllegalStateException("Coordinate x cannot be null");
        }
        if (y <= -365) {
            throw new IllegalStateException("Coordinate y should be greater than -365");
        }
        return new Coordinates(x, y);
    }
}