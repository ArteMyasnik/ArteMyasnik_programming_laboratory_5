package com.artemyasnik.network.server;

import com.artemyasnik.chat.Router;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import com.artemyasnik.io.workers.console.ConsoleWorker;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Server implements Runnable, AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(Server.class);
    private final ServerConfiguration config;
    private final ConsoleWorker console;
    private DatagramChannel channel;
    private Selector selector;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Queue<ByteBuffer> bufferPool = new ConcurrentLinkedQueue<>();
    private final Queue<ClientResponse> responseQueue = new ConcurrentLinkedQueue<>();

    public Server(ServerConfiguration config, ConsoleWorker console) {
        this.config = config;
        this.console = console;
        initServer();
    }

    private void initServer() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(config.port()));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);

            log.info("Server started with configuration: {}", config);
        } catch (IOException e) {
            log.error("Server initiation error: {}", e.getMessage());
        }
    }

    private ByteBuffer getBuffer() {
        ByteBuffer buf = bufferPool.poll();
        return buf != null ? buf : ByteBuffer.allocate(config.bufferSize());
    }

    private void releaseBuffer(ByteBuffer buffer) {
        buffer.clear();
        bufferPool.offer(buffer);
    }

    @Override
    public void run() {
        try {
            while (isRunning.get()) {
                int readyChannels = selector.select(1000);
                if (readyChannels == 0) continue;

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isReadable()) {
                        handleRequest(key);
                    }

                    if (key.isWritable()) {
                        handleResponse(key);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Server running error: {}", e.getMessage());
        } finally {
            close();
        }
    }

    private void handleRequest(SelectionKey key) throws IOException {
        ByteBuffer buffer = getBuffer();
        try {
            buffer.clear();
            InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

            if (clientAddress != null) {
                buffer.flip();
                Request request = SerializationUtils.deserialize(buffer.array());
                log.info("Received request from {}: {} - {}", String.valueOf(clientAddress), request.command(), request);

                log.info("Command received: {}", request.command());
                Response response = processRequest(request);
                log.info("Response: {}", response);
                responseQueue.add(new ClientResponse(response, clientAddress));
                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//                sendResponse(response, clientAddress, buffer);
            }
        } finally {
            releaseBuffer(buffer);
        }
    }

    private Response processRequest(Request request) {
        return Router.getInstance().route(request);
    }

    private void handleResponse(SelectionKey key) throws IOException {
        if (!responseQueue.isEmpty()) {
            ClientResponse clientResponse = responseQueue.poll();
            sendResponse(clientResponse);
        }

        if (responseQueue.isEmpty()) {
            key.interestOps(SelectionKey.OP_READ);
        }

    }

    private void sendResponse(ClientResponse clientResponse) throws IOException {
        ByteBuffer buffer = getBuffer();
        try {
            byte[] responseBytes = SerializationUtils.serialize(clientResponse.response());
            buffer.clear();
            buffer.put(responseBytes);
            buffer.flip();
            channel.send(buffer, clientResponse.address());
        } finally {
            releaseBuffer(buffer);
        }
    }

    public void stop() {
        isRunning.set(false);
    }

    @Override
    public void close() {
        isRunning.set(false);
        try {
            if (selector != null) selector.close();
            if (channel != null) channel.close();
            log.warn("Server stopped");
        } catch (IOException e) {
            log.error("Error closing resources: {}", e.getMessage());
        }
    }

    private record ClientResponse(Response response, InetSocketAddress address) {
    }
}