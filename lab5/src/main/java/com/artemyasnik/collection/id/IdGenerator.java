package com.artemyasnik.collection.id;

import com.artemyasnik.io.configuration.FileConfiguration;
import com.artemyasnik.io.workers.file.BufferedFileWorker;
import com.artemyasnik.io.workers.file.FileWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private static final Logger log = LoggerFactory.getLogger(IdGenerator.class);
    private static IdGenerator INSTANCE;
    private final AtomicInteger currentId;
    private final FileWorker fileWorker;

    private IdGenerator() throws IOException {
        this.fileWorker = new BufferedFileWorker(FileConfiguration.ID_SEQ_FILE_PATH);
        this.currentId = new AtomicInteger(loadLastId());
        validateId(currentId.get());
    }

    public static synchronized IdGenerator getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new IdGenerator();
            } catch (IOException e) {
                log.error("Failed to initialize IdGenerator", e);
                throw new RuntimeException("IdGenerator initialization failed", e);
            }
        }
        return INSTANCE;
    }

    private int loadLastId() throws IOException {
        try {
            if (!fileWorker.ready()) {
                return 1;
            }

            String content = fileWorker.read();
            if (content == null || content.trim().isEmpty()) {
                return 1;
            }

            int id = Integer.parseInt(content.trim());
            validateId(id);
            return id;
        } catch (NumberFormatException e) {
            log.warn("Invalid ID format in file, resetting to 1");
            saveLastId(1);
            return 1;
        }
    }

    public void saveLastId(int id) throws IOException {
        validateId(id);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FileConfiguration.ID_SEQ_FILE_PATH.toFile(), false))) {
            writer.write(Integer.toString(id));
            writer.flush();
        }
        log.debug("Saved last ID: {} to file", id);
    }

    private void validateId(int id) {
        if (id < 1) {
            throw new IllegalArgumentException("ID cannot be negative. Got: " + id);
        }
    }

    public int generateId() {
        int newId = currentId.incrementAndGet();
        try {
            saveLastId(newId);
            log.debug("Generated new ID: {}", newId);
        } catch (IOException e) {
            log.error("Failed to save last ID", e);
        }
        return newId;
    }

    public void initializeWith(int initialValue) {
        validateId(initialValue);
        currentId.set(initialValue);
        try {
            saveLastId(initialValue);
            log.debug("Initialized ID generator with value: {}", initialValue);
        } catch (IOException e) {
            log.error("Failed to save initial ID", e);
        }
    }

    public static int getCurrentId() {
        return getInstance().currentId.get();
    }

    public void close() {
        try {
            fileWorker.close();
            log.debug("ID generator resources closed");
        } catch (Exception e) {
            log.error("Error closing ID generator resources", e);
        }
    }
}
