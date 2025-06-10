package com.artemyasnik.network.client;

import com.artemyasnik.chat.Router;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.io.IOWorker;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import com.artemyasnik.io.workers.console.ConsoleWorker;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;

import static com.artemyasnik.collection.util.InputUtil.get;

public final class Client implements Runnable {
    private final static Logger log = LoggerFactory.getLogger(Client.class);
    private final ClientConfiguration config;
    private final ConsoleWorker console;
    private final IOWorker<String> script;
    private DatagramSocket socket;
    private InetSocketAddress serverAddress;
    private static final int RESPONSE_TIMEOUT = 5000;
    private static final int MAX_REQUEST_ATTEMPTS = 3;

    public Client(ClientConfiguration config, ConsoleWorker console, IOWorker<String> script) {
        this.config = config;
        this.console = console;
        this.script = script;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(RESPONSE_TIMEOUT);
            serverAddress = new InetSocketAddress(config.host(), config.port());

            log.info("Client started with configuration: {}", config);

            console.writeln("Welcome to lab6 by ArteMyasnik!");
            String line;
            while ((line = console.read("$ ")) != null) {
                handleInput(line);
                while (!script.ready()) handleInput(script.read());
            }
        } catch (IOException e) {
            log.error("Client error: {}", e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void handleInput(String line) {
        if (line == null || line.isBlank()) return;
        if (line.equalsIgnoreCase("exit")) {
            closeResources();
            System.exit(0);
        }

        Request request = parseRequest(line);
        if (request == null) return;

        try {
            Response response = sendRequestWithRetry(request);
            if (response != null) {
                handleResponse(response);
            } else {
                log.warn("Server is not responding, please try again later");
                console.writeln("Server is not responding, please try again later");
            }
        } catch (IOException e) {
            log.error("Network error: {}", e.getMessage());
            console.writeln("Network error occurred: " + e.getMessage());
        }
    }

    private Request parseRequest(final String line) {
        final String[] parts = line.split(" ", 2);

        String command = parts[0];
        List<String> args = parts.length > 1 ? Arrays.asList(parts[1].split(" ")) : Collections.emptyList();
        final List<StudyGroup> studyGroup = new LinkedList<>();

        int elementRequired = Router.getInstance().getElementRequired(command);

        while (elementRequired-- > 0) {
            try {
                studyGroup.add(get(script.ready() ? console : script));
            } catch (InterruptedException e) {
                log.error("\nCommand interrupted: {}\n", e.getMessage());
                return null;
            } catch (IOException ex) {
                log.error("IOException: {}", ex.getMessage());
                return null;
            }
        }

        return new Request(command, args, studyGroup);
    }

    private Response sendRequestWithRetry(Request request) throws IOException {
        int attempts = 0;
        while (true) {
            attempts++;
            try {
                sendRequest(request);
                return receiveResponse();
            } catch (SocketTimeoutException e) {
                log.warn("Attempt {}: Server response timeout", attempts);
                if (attempts >= MAX_REQUEST_ATTEMPTS) {
                    throw new IOException("Server is not responding after " + MAX_REQUEST_ATTEMPTS + " attempts");
                }
            }
        }
    }

    private void sendRequest(Request request) throws IOException {
        byte[] requestBytes = SerializationUtils.serialize(request);
        DatagramPacket packet = new DatagramPacket(
                requestBytes,
                requestBytes.length,
                serverAddress
        );
        socket.send(packet);
    }

    private Response receiveResponse() throws IOException {
        byte[] buffer = new byte[config.bufferSize()];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return SerializationUtils.deserialize(packet.getData());
    }

    private void handleResponse(Response response) {
        if (response.message() != null && !response.message().isBlank()) console.writeln(response.message());
        if (response.studyGroup() != null && !response.studyGroup().isEmpty())
            response.studyGroup().stream().map(StudyGroup::toString).forEach(console::writeln);
        if (response.script() != null && !response.script().isEmpty()) script.insert(response.script());
    }

    private void closeResources() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            log.info("Client stopped");
        }
    }
}