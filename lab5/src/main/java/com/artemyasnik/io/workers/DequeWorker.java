package com.artemyasnik.io.workers;

import com.artemyasnik.io.IOWorker;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public final class DequeWorker implements IOWorker<String> {
    private final Deque<String> deque = new ArrayDeque<>();


    @Override
    public String read() {
        return deque.pollFirst();
    }

    @Override
    public void write(final String data) {
    }

    @Override
    public void close() {
        deque.clear();
    }

    @Override
    public boolean ready() {
        return deque.isEmpty();
    }

    @Override
    public void insert(String data) {
        deque.addAll(Arrays.asList(data.split(System.lineSeparator())));
    }
}
