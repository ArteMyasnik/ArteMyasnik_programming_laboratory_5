package com.artemyasnik.network.client;

import com.artemyasnik.chat.Router;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.db.dao.UserDAO;
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
import java.sql.SQLException;
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
    private final UserDAO userDAO = new UserDAO();
    private UserDTO currentUser;

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
            console.writeln("Welcome to lab7 by ArteMyasnik!");

            if (!authenticateUser()) {
                console.writeln("Authentication failed. Exiting...");
                return;
            }

            String line;
            while ((line = console.read("$ ")) != null) {
                final String currentLine = line;
                requestSenderPool.submit(() -> handleInput(currentLine));
                while (!script.ready()) {
                    String scriptLine = script.read();
                    final String currentScriptLine = scriptLine;
                    requestSenderPool.submit(() -> handleInput(currentScriptLine));
                }
                processResponses();
            }
        } catch (IOException e) {
            log.error("Client error: {}", e.getMessage());
        } finally {
            closeResources();
        }
    }

    private boolean authenticateUser() throws IOException {
        while (true) {
            console.writeln("Choose action:");
            console.writeln("1. Login");
            console.writeln("2. Register");
            console.writeln("3. Exit");

            String choice = console.read("$ ").trim();

            try {
                switch (choice) {
                    case "1":
                        return login();
                    case "2":
                        return register();
                    case "3":
                        return false;
                    default:
                        console.writeln("Invalid choice. Please try again.");
                }
            } catch (SQLException e) {
                console.writeln("Database error: " + e.getMessage());
                return false;
            }
        }
    }

    private boolean login() throws SQLException, IOException {
        console.writeln("=== Login ===");
        String username = console.read("Username: ").trim();
        String password = console.read("Password: ").trim();

        Optional<UserDTO> user = userDAO.authenticate(username, password);
        if (user.isPresent()) {
            currentUser = user.get();
            console.writeln("Login successful! Welcome, " + username);
            return true;
        } else {
            console.writeln("Invalid username or password");
            return false;
        }
    }

    private boolean register() throws SQLException, IOException {
        console.writeln("=== Registration ===");
        String username = console.read("Choose username: ").trim();
        String password = console.read("Choose password: ").trim();
        String confirmPassword = console.read("Confirm password: ").trim();

        if (!password.equals(confirmPassword)) {
            console.writeln("Passwords do not match");
            return false;
        }

        try {
            currentUser = userDAO.registerUser(username, password);
            console.writeln("Registration successful! Welcome, " + username);
            return true;
        } catch (IllegalArgumentException e) {
            console.writeln("Error: " + e.getMessage());
            return false;
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

        return new Request(command, args, studyGroup, currentUser);
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