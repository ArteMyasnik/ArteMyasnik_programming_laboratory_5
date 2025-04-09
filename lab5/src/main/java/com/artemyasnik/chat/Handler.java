package com.artemyasnik.chat;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.io.IOWorker;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import com.artemyasnik.io.workers.console.ConsoleWorker;

import java.io.IOException;
import java.util.*;

import static com.artemyasnik.collection.util.InputUtil.get;

public final class Handler implements Runnable {
    private final ConsoleWorker console;
    private final IOWorker<String> script;

    public Handler(final ConsoleWorker console, final IOWorker<String> script) {
        this.console = console;
        this.script = script;
    }

    @Override
    public void run() {
        CollectionManager.getInstance();
        console.writeln("Welcome to lab5 by ArteMyasnik!");
        try {
            String line;
            while ((line = console.read("$ ")) != null) {
                handle(line);
                while (!script.ready()) handle(script.read());
            }
        } catch (Exception e) {
            console.writef("Error: %s%n" + e.getMessage());
        }
    }

    private void handle(String line) {
        if (line == null || line.isBlank()) return;
        print(Router.getInstance().route(parse(line)));
    }

    private Request parse(final String line) {
        final String[] parts = line.split(" ", 2);

        String command = parts[0];
        List<String> args = parts.length > 1 ? Arrays.asList(parts[1].split(" ")) : Collections.emptyList();
        final List<StudyGroup> studyGroup = new LinkedList<>();

        int elementRequired = Router.getInstance().getElementRequired(command);

        while (elementRequired-- > 0) {
            try {
                studyGroup.add(get(script.ready() ? console : script));
            } catch (InterruptedException e) {
                console.writeln("%nCommand interrupted: %s%n".formatted(e.getMessage()));
                return null;
            } catch (IOException ex) {
                console.writeln("IOException: %s".formatted(ex.getMessage()));
                return null;
            }
        }

        return new Request(command, args, studyGroup);
    }

    private void print(final Response response) {
        if (response.message() != null && !response.message().isBlank()) console.writeln(response.message());
        if (response.studyGroup() != null && !response.studyGroup().isEmpty())
            response.studyGroup().stream().map(StudyGroup::toString).forEach(console::writeln);
        if (response.script() != null && !response.script().isEmpty()) script.insert(response.script());
    }
}
