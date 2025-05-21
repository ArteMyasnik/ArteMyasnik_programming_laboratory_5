package com.artemyasnik.network.server;

import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
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

public class Server implements Runnable, AutoCloseable {
    private final ServerConfiguration config;
    private final ConsoleWorker console;
    private DatagramChannel channel;
    private Selector selector;
    private volatile boolean running = true;

    public Server(ServerConfiguration config, ConsoleWorker console) {
        this.config = config;
        this.console = console;
    }

    @Override
    public void run() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(config.port()));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            console.writeln("Server started with configuration: " + config);

            ByteBuffer buffer = ByteBuffer.allocate(config.bufferSize());

            while (running) {
                int readyChannels = selector.select(1000);
                if (readyChannels == 0) continue;

                var keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isReadable()) {
                        handleRequest(buffer);
                    }
                }
            }
        } catch (IOException e) {
            console.writeln("Server error: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void handleRequest(ByteBuffer buffer) throws IOException {
        buffer.clear();
        InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

        if (clientAddress != null) {
            try {
                buffer.flip();
                Request request = SerializationUtils.deserialize(buffer.array());
                console.writeln("Received request from %s: %s", String.valueOf(clientAddress), request.command());

                Response response = processRequest(request);
                sendResponse(response, clientAddress, buffer);
            } catch (ClassNotFoundException e) {
                console.writeln("Deserialization error: " + e.getMessage());
            }
        }
    }

    private void sendResponse(Response response, InetSocketAddress clientAddress, ByteBuffer buffer) throws IOException {
        byte[] responseBytes = SerializationUtils.serialize(response);
        buffer.clear();
        buffer.put(responseBytes);
        buffer.flip();
        channel.send(buffer, clientAddress);
    }

    private Response processRequest(Request request) {
        // Ваша логика обработки запроса-------------------------------------------------------------------------------
        return new Response("Command processed: " + request.command());
    }

    public void stop() {
        running = false;
    }

    @Override
    public void close() {
        running = false;
        try {
            if (selector != null) selector.close();
            if (channel != null) channel.close();
            console.writeln("Server stopped");
        } catch (IOException e) {
            console.writeln("Error closing resources: " + e.getMessage());
        }
    }
}