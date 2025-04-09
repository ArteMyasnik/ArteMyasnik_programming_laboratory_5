package com.artemyasnik.io.workers.file;

import java.io.*;
import java.nio.file.Path;

public final class BufferedFileWorker implements FileWorker {
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public BufferedFileWorker(
            final Path path,
            final boolean append
    ) throws IOException {
        this.reader = new BufferedReader(new FileReader(path.toFile()));
        this.writer = new BufferedWriter(new FileWriter(path.toFile(), append));
    }

    public BufferedFileWorker(final Path path) throws IOException {
        this(path, true);
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
    public void write(final String data) {
        try {
            writer.append(data).flush();
        } catch (IOException ignored) {
        }
    }
}
