package com.artemyasnik.io.file;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class MyInputStreamReader {

    private final Path filePath;

    public MyInputStreamReader(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Читает содержимое файла в виде строки.
     *
     * @return Содержимое файла в виде строки.
     * @throws IOException Если произошла ошибка при чтении файла.
     */
    public String readFile() throws IOException {
        StringBuilder content = new StringBuilder();

        // Используем InputStreamReader для чтения файла с указанной кодировкой
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int bytesRead;

            while ((bytesRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, bytesRead);
            }
        }

        return content.toString();
    }
}
