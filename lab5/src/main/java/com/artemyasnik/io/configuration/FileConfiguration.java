package com.artemyasnik.io.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileConfiguration {
    private static FileConfiguration instance;
    public static Path DATA_FILE_PATH;
    public static final Path ID_SEQ_FILE_PATH = Path.of("ID_SEQ");
    private final static Logger log = LoggerFactory.getLogger(FileConfiguration.class);
    static {
        DATA_FILE_PATH = null;
    }
    private FileConfiguration() {
    }

    public static FileConfiguration getInstance() {
        return instance == null ? instance = new FileConfiguration() : instance;
    }

    public static void initialize(final String path) {
        DATA_FILE_PATH = Paths.get(path).toAbsolutePath();
        checkFile(DATA_FILE_PATH);
        checkFile(ID_SEQ_FILE_PATH);
    }

    private static void checkFile(Path file) {
        if (file == null)
            throw new IllegalStateException("Путь к файлу не инициализирован. Вызовите FileConfiguration.initialize().");
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
                log.info("Файл {} был создан{}", file, System.lineSeparator());
            } catch (IOException e) {
                log.error("Ошибка при создании файла {}: {}{}", file, e.getMessage(), System.lineSeparator());
                System.exit(1);
            }
        }
        if (!Files.isRegularFile(file)) {
            log.error("{} не является файлом{}", file, System.lineSeparator());
            System.exit(1);
        }
        if (!Files.isReadable(file)) log.error("Файл {} недоступен для чтения{}", file, System.lineSeparator());
        if (!Files.isWritable(file)) log.error("Файл {} недоступен для записи{}", file, System.lineSeparator());
        log.info("Файл {} успешно проверен{}", file, System.lineSeparator());
    }
}
