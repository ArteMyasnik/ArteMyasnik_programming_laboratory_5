package com.artemyasnik.app;

import com.artemyasnik.io.workers.console.ConsoleWorker;
import com.artemyasnik.io.workers.console.BufferedConsoleWorker;
import com.artemyasnik.network.server.ServerConfiguration;
import com.artemyasnik.network.server.Server;

public class ServerApp {
    public static void main(String[] args) {
        ConsoleWorker consoleWorker = new BufferedConsoleWorker();
        ServerConfiguration config = new ServerConfiguration(9876, 8192);

        try (Server server = new Server(config, consoleWorker)) {
            new Thread(server).start();

            // Ожидание команды для остановки сервера
            consoleWorker.read("Нажмите Enter для остановки сервера...");
            server.stop();
        }
    }
}