package com.artemyasnik;

import com.artemyasnik.chat.Handler;
import com.artemyasnik.io.IOWorker;
import com.artemyasnik.io.configuration.FileConfiguration;
import com.artemyasnik.io.workers.console.BufferedConsoleWorker;
import com.artemyasnik.io.workers.console.ConsoleWorker;
import com.artemyasnik.io.workers.DequeWorker;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("There is no data file. Type 'java -jar <jarFile>.jar <fileName>'");
            System.exit(1);
        }

        if (args.length > 1) {
            System.err.println("Too many arguments. Type 'java -jar <jarFile>.jar <fileName>'");
            System.exit(1);
        }

        final String filePath = args[0];
        FileConfiguration.getInstance();
        FileConfiguration.initialize(filePath);

        try (ConsoleWorker console = new BufferedConsoleWorker(); IOWorker<String> deque = new DequeWorker()) {
            new Handler(console, deque).run();
        } catch (Exception e) {
            System.err.printf("Error: %s%n", e.getMessage());
            System.exit(1);
        }
    }
}