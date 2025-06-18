package com.artemyasnik.app;

import com.artemyasnik.io.workers.console.ConsoleWorker;
import com.artemyasnik.io.workers.console.BufferedConsoleWorker;
import com.artemyasnik.network.server.ServerConfiguration;
import com.artemyasnik.network.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServerApp {
    private static final Logger log = LoggerFactory.getLogger(ServerApp.class);

    public static void main(String[] args) {
        ServerConfiguration config = new ServerConfiguration(9876, 8192);
        ConsoleWorker consoleWorker = new BufferedConsoleWorker();

        try (Server server = new Server(config, consoleWorker)) {
            server.registerShutdownHook();

            new Thread(server, "Server Thread").start();
            consoleWorker.write("Server started.\n");
            handleAdminCommands(server, consoleWorker);

        } catch (Exception e) {
            log.error("Server fatal error: {}", e.getMessage());
            System.exit(1);
        }
    }

    private static void handleAdminCommands(Server server, ConsoleWorker consoleWorker) {
        while (server.isRunning()) {
            try {
                String input = consoleWorker.read("$ ").trim().toLowerCase();

                switch (input) {
                    case "exit":
                        consoleWorker.write("Shutting down server...\n");
                        server.stop();
                        break;
                    case "status":
                        consoleWorker.write("Server status: " +
                                (server.isRunning() ? "RUNNING" : "STOPPED") + "\n");
                        break;
                    default:
                        consoleWorker.write("Unknown command. Type 'help' for available commands.\n");
                }
            } catch (Exception e) {
                log.warn("Error processing admin command: {}", e.getMessage());
            }
        }
    }
}