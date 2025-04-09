package com.artemyasnik.io.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileConfiguration {
    private static FileConfiguration instance;
    public static Path DATA_FILE_PATH;
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
        checkFile();
    }

    private static void checkFile() {
        if (DATA_FILE_PATH == null)
            throw new IllegalStateException("Путь к файлу не инициализирован. Вызовите FileConfiguration.initialize().");
        if (!Files.exists(DATA_FILE_PATH)) {
            try {
                Files.createFile(DATA_FILE_PATH);
                System.out.printf("Файл %s был создан%n", DATA_FILE_PATH);
            } catch (IOException e) {
                System.err.printf("Ошибка при создании файла %s: %s%n", DATA_FILE_PATH, e.getMessage());
                System.exit(1);
            }
        }
        if (!Files.isRegularFile(DATA_FILE_PATH)) {
            System.err.printf("%s не является файлом%n", DATA_FILE_PATH);
            System.exit(1);
        }
        if (!Files.isReadable(DATA_FILE_PATH)) System.err.printf("Файл %s недоступен для чтения%n", DATA_FILE_PATH);
        if (!Files.isWritable(DATA_FILE_PATH)) System.err.printf("Файл %s недоступен для записи%n", DATA_FILE_PATH);
        System.out.printf("Файл %s успешно проверен%n", DATA_FILE_PATH);
    }
}
