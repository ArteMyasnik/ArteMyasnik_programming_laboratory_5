package com.artemyasnik.io.workers.console;

import java.io.*;

public final class BufferedConsoleWorker implements ConsoleWorker {
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

    @Override
    public void close() throws Exception {
        reader.close();
        writer.close();
    }

    @Override
    public boolean ready() {
        try {
            return reader.ready();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String read() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void write(String data) {
        try {
            writer.append(data).flush();
        } catch (IOException ignored) {
        }
    }
}
