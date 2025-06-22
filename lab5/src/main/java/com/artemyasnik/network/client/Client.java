package com.artemyasnik.network.client;

import com.artemyasnik.chat.Router;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.db.dto.UserDTO;
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
import java.util.concurrent.*;

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
    private final ExecutorService requestSenderPool = Executors.newCachedThreadPool();
    private final ForkJoinPool responseProcessorPool = new ForkJoinPool();
    private final Queue<Response> responseQueue = new LinkedBlockingQueue<>();

    private int loginAttempts = 0;
    private UserDTO userDTO = null;

    public Client(ClientConfiguration config, ConsoleWorker console, IOWorker<String> script) {
        this.config = config;
        this.console = console;
        this.script = script;
    }

    private void init() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(RESPONSE_TIMEOUT);
        serverAddress = new InetSocketAddress(config.host(), config.port());

        log.info("Client started with configuration: {}", config);
        console.writeln("Welcome to lab7 by ArteMyasnik!");
    }


    private void getUser() {
        if (loginAttempts > 5) {
            console.writeln("Attempts exceeded.");
            System.exit(1);
        }

        console.writeln("Enter username and password to authorize. Attempts left: " + (5 - loginAttempts));
        final String username = console.read("Enter login: ");
        final String password = console.read("Enter password: ");

        userDTO = UserDTO.register(username, password);
        try {
            sendRequest(new Request("help", Collections.emptyList(), Collections.emptyList(), userDTO));
            Response response = receiveResponse();
            if (response.message().contains("Authorization failed.")) {
                loginAttempts++;
                console.writeln(response.message());
                getUser();
            }
            console.writeln("You logged in successfully!");
        } catch (IOException e) {
            console.writeln("Failed to send request: " + e);
        }
    }

    @Override
    public void run() {
        try {
            init();
            getUser();
            String line;
            while ((line = console.read("$ ")) != null) {
                final String currentLine = line;
                requestSenderPool.submit(() -> handleInput(currentLine));
                while (!script.ready()) {
                    final String currentScriptLine = script.read();
                    requestSenderPool.submit(() -> handleInput(currentScriptLine));
                }
                processResponses();
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
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
        log.info("Request: {}", request);
        if (request == null) return;

        try {
            Response response = sendRequestWithRetry(request);
            log.info("Response: {}", response);
            if (response != null) {
                responseQueue.offer(response);
            } else {
                log.warn("Server is not responding, please try again later");
                console.writeln("Server is not responding, please try again later");
            }
        } catch (IOException e) {
            log.error("Network error: {}", e.getMessage());
            console.writeln("Network error occurred: " + e.getMessage());
        }
    }

    private void processResponses() {
        synchronized (responseQueue) {
            while (!responseQueue.isEmpty()) {
                Response response = responseQueue.poll();
                responseProcessorPool.submit(() -> handleResponse(response));
            }
        }
    }

    private Request parseRequest(final String line) {
        final String[] parts = line.split(" ", 2);

        String command = parts[0];
        List<String> args = parts.length > 1 ? Arrays.asList(parts[1].split(" ")) : Collections.emptyList();
        final List<StudyGroup> studyGroup = Collections.synchronizedList(new LinkedList<>());
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
        return new Request(command, args, studyGroup, userDTO);
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
        }
        requestSenderPool.shutdownNow();
        responseProcessorPool.shutdownNow();
        log.info("Client stopped");
    }
}