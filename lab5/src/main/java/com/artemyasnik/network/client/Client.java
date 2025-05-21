package com.artemyasnik.network.client;

import com.artemyasnik.chat.Router;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.io.IOWorker;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import com.artemyasnik.io.workers.DequeWorker;
import com.artemyasnik.io.workers.console.ConsoleWorker;
import org.apache.commons.lang3.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.artemyasnik.collection.util.InputUtil.get;

public class Client implements Runnable {
    private final ClientConfiguration config;
    private final ConsoleWorker console;
    private final IOWorker<String> script;
    private DatagramChannel channel;
    private Selector selector;

    public Client(ClientConfiguration config, ConsoleWorker console, IOWorker<String> script) {
        this.config = config;
        this.console = console;
        this.script = script;
    }

    @Override
    public void run() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(config.host(), config.port()));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            console.writeln("Client started with configuration: " + config);

            try {
                console.writeln("Welcome to lab6 by ArteMyasnik!");
                String line;
                while ((line = console.read("$ ")) != null) {
                    handleInput(line);
                    while (!script.ready()) handleInput(script.read());
                }
            } catch (Exception e) {
                console.writef("Error: %s%n" + e.getMessage());
            }
        } catch (IOException e) {
            console.writeln("Client error: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void handleInput(String line) throws IOException {
        if (line == null || line.isBlank()) return;
        if (line.equalsIgnoreCase("exit")) {
            closeResources();
            System.exit(0);
        }

        Request request = parseRequest(line);
        if (request == null) return;
        sendRequest(request);

        Response response = receiveResponse();
        handleResponse(response);
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
                console.writeln("%nCommand interrupted: %s%n".formatted(e.getMessage()));
                return null;
            } catch (IOException ex) {
                console.writeln("IOException: %s".formatted(ex.getMessage()));
                return null;
            }
        }

        return new Request(command, args, studyGroup);
    }

    private void sendRequest(Request request) throws IOException {
        byte[] requestBytes = SerializationUtils.serialize(request);
        ByteBuffer buffer = ByteBuffer.allocate(4 + requestBytes.length);
        buffer.put(requestBytes);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    private Response receiveResponse() throws IOException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        readFromChannel(lengthBuffer);
        lengthBuffer.flip();
        int messageLength = lengthBuffer.getInt();
        ByteBuffer allocated = ByteBuffer.allocate(messageLength);
        readFromChannel(allocated);
        allocated.flip();
        return SerializationUtils.deserialize(allocated.array());
    }

    private void readFromChannel(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) {
                throw new IOException("Server has closed the connection");
            }
        }
    }

    private void handleResponse(Response response) {
        if (response.message() != null && !response.message().isBlank()) console.writeln(response.message());
        if (response.studyGroup() != null && !response.studyGroup().isEmpty())
            response.studyGroup().stream().map(StudyGroup::toString).forEach(console::writeln);
        if (response.script() != null && !response.script().isEmpty()) script.insert(response.script());
    }

    private void closeResources() {
        try {
            if (selector != null) selector.close();
            if (channel != null && channel.isOpen()) channel.close();
            console.writeln("Client stopped");
        } catch (IOException e) {
            console.writeln("Error closing resources: " + e.getMessage());
        }
    }
}