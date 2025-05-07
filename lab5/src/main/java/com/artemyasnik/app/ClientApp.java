package com.artemyasnik.app;

import com.artemyasnik.io.workers.console.ConsoleWorker;
import com.artemyasnik.io.workers.console.BufferedConsoleWorker;
import com.artemyasnik.network.client.ClientConfiguration;
import com.artemyasnik.network.client.Client;

public class ClientApp {
    public static void main(String[] args) {
        ConsoleWorker consoleWorker = new BufferedConsoleWorker();
        ClientConfiguration config = new ClientConfiguration("localhost", 9876, 8192);

        Client client = new Client(config, consoleWorker);
        client.run(); // Запуск в текущем потоке
    }
}