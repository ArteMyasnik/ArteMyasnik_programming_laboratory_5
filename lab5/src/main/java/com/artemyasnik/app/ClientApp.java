package com.artemyasnik.app;

import com.artemyasnik.io.IOWorker;
import com.artemyasnik.io.workers.DequeWorker;
import com.artemyasnik.io.workers.console.ConsoleWorker;
import com.artemyasnik.io.workers.console.BufferedConsoleWorker;
import com.artemyasnik.network.client.ClientConfiguration;
import com.artemyasnik.network.client.Client;

public final class ClientApp {
    public static void main(String[] args) {
        ConsoleWorker consoleWorker = new BufferedConsoleWorker();
        IOWorker<String> script = new DequeWorker();
        ClientConfiguration config = new ClientConfiguration("localhost", 9876, 8192);
        Client client = new Client(config, consoleWorker, script);
        try {
            new Thread(client).start();
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } finally {
            client.closeResources();
        }
    }
}