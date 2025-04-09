package com.artemyasnik.io.workers.file;

import com.artemyasnik.io.IOWorker;

public interface FileWorker extends IOWorker<String> {
    default String readAll() {
        StringBuilder text = new StringBuilder();
        while (ready()) {
            text.append(read()).append(System.lineSeparator());
        }
        return text.toString();
    }
}
