package com.artemyasnik.app;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.configuration.FileConfiguration;
import com.artemyasnik.io.workers.console.ConsoleWorker;
import com.artemyasnik.io.workers.console.BufferedConsoleWorker;
import com.artemyasnik.network.server.ServerConfiguration;
import com.artemyasnik.network.server.Server;

import java.io.IOException;

public final class ServerApp {
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

        ConsoleWorker consoleWorker = new BufferedConsoleWorker();
        ServerConfiguration config = new ServerConfiguration(9876, 8192);

        try (Server server = new Server(config, consoleWorker)) {
            server.registerShutdownHook();
            new Thread(server).start();

            consoleWorker.read("Press Enter to stop server..." + System.lineSeparator());
            server.stop();
        }

        try (Server server = new Server(config, consoleWorker)) {
            server.registerShutdownHook();
            new Thread(server).start();
            consoleWorker.write("Server started. Type 'help' for available commands.\n");

            while (server.isRunning()) {
                String input = consoleWorker.read("$ ");
                if (input == null) continue;

                input = input.trim().toLowerCase();
                switch (input) {
                    case "exit":
                        consoleWorker.write("Shutting down server...\n");
                        server.stop();
                        break;
                    case "save":
                        consoleWorker.write("Saving data...\n");
                        CollectionManager.getInstance().save();
                        consoleWorker.write("Data saved successfully.\n");
                        break;
                    default:
                        consoleWorker.write("Unknown command. Type 'help' for available commands.\n");
                }
            }
        }
    }
}