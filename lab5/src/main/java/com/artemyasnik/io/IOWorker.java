package com.artemyasnik.io;

public interface IOWorker<T> extends AutoCloseable {
    T read();

    default T read(T prompt) {
        write(prompt);
        return read();
    }

    void write(T data);

    void close() throws Exception;

    boolean ready();

    default void insert(T data) {
    }
}
