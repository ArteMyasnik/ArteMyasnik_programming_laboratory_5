package lab.laboratory5;

import java.util.LinkedList;

public class Receiver {
    private final LinkedList<Integer> queue = new LinkedList<>();

    private static final Receiver INSTANCE = new Receiver();

    private Receiver() {}

    public static Receiver getInstance() {
        return INSTANCE;
    }
}
