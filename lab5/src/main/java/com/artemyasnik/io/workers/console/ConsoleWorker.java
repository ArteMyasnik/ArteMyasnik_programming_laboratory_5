package com.artemyasnik.io.workers.console;

import com.artemyasnik.io.IOWorker;

public interface ConsoleWorker extends IOWorker<String> {
    default void writeln(String data){
        write(data.concat(System.lineSeparator()));
    }

    default void writeln(String... lines) {
        write(String.join(System.lineSeparator(), lines).concat(System.lineSeparator()));
    }

    default void writef(String format, Object... args) {
        write(String.format(format, args));
    }

    @Override
    default String read(String prompt) {
        return IOWorker.super.read(prompt.concat(""));
    }
}
