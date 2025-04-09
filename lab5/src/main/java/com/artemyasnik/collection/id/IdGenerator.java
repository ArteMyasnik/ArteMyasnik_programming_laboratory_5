package com.artemyasnik.collection.id;

public final class IdGenerator {
    private static final IdGenerator INSTANCE = new IdGenerator();
    private int currentId;

    private IdGenerator() {
        currentId = 0;
    }

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public int generateId() {
        return ++currentId;
    }
}