package com.artemyasnik.command;

import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import com.artemyasnik.io.workers.file.BufferedFileWorker;
import com.artemyasnik.io.workers.file.FileWorker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public final class ExecuteScript extends Command {
    ExecuteScript() {
        super("execute_script", new String[]{"filePath"}, "execute script of commands from file");
    }

    @Override
    public Response execute(Request request) {
        if (request.args() == null || request.args().isEmpty()) {
            return new Response("No file to execute");
        }

        final Path path = Paths.get(request.args().get(0));

        if (!Files.exists(path)) return new Response("File doesn't exist");
        if (!Files.isReadable(path)) return new Response("File cannot be read");
        if (!Files.isRegularFile(path)) return new Response("File isn't a file");

        try (FileWorker file = new BufferedFileWorker(path)) {
            StringBuilder script = new StringBuilder();
            while (file.ready()) {
                script.append(file.read()).append(System.lineSeparator());
            }
            return new Response("Script loaded successfully", Collections.emptyList(), script.toString());
        } catch (Exception e) {
            return new Response("Error: %s%n".formatted(e.getMessage()));
        }
    }
}
