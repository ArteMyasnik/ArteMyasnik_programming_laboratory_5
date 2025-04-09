package com.artemyasnik.io.file;

import java.io.IOException;
import java.io.FileWriter;
import java.nio.file.Path;

public final class MyFileWriter {

    private final Path filePath;

    public MyFileWriter(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Записывает строку в файл.
     *
     * @param content Строка для записи в файл.
     * @throws IOException Если произошла ошибка при записи в файл.
     */
    public void writeFile(String content) throws IOException {
        // Используем FileWriter для записи данных в файл
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(content);
        }
    }
}
