package com.artemyasnik.network.server;

import com.artemyasnik.chat.Router;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import com.artemyasnik.io.workers.console.ConsoleWorker;
import lombok.Getter;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Server implements Runnable, AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(Server.class);
    private final ServerConfiguration config;
    private final ConsoleWorker console;
    private DatagramChannel channel;
    private Selector selector;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    private final ExecutorService requestReadingPool = Executors.newCachedThreadPool();
    private final ForkJoinPool requestProcessingPool = new ForkJoinPool();
    private final ForkJoinPool responseSendingPool = new ForkJoinPool();
    private final Queue<RequestTask> requestQueue = new ConcurrentLinkedQueue<>();
    private final Queue<ByteBuffer> bufferPool = new ConcurrentLinkedQueue<>();
    private final Queue<ClientResponse> responseQueue = new ConcurrentLinkedQueue<>();
    private final static int MAX_BUFFER_POOL_SIZE = 50;
    private final static int MAX_RESPONSE_QUEUE_SIZE = 1000;
    private final static int MAX_RESPONSE_ATTEMPTS = 3;
    private final static long GRACEFUL_SHUTDOWN_TIMEOUT = 5000;
    @Getter
    private volatile boolean initializationFailed = false;

    public Server(ServerConfiguration config, ConsoleWorker console) {
        this.config = config;
        this.console = console;
        initServer();
    }

    private void initServer() {
        try {
            log.info("Initializing server...");
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            try {
                channel.bind(new InetSocketAddress(config.port()));
            } catch (IOException e) {
                channel.close();
                throw new IOException("Port " + config.port() + " is already in use", e);
            }
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            startRequestProcessingThread();
            startResponseHandlingThread();
            log.info("Server started with configuration: {}", config);
        } catch (IOException e) {
            initializationFailed = true;
            log.error("Server initiation error: {}", e.getMessage());
            close();
            throw new RuntimeException("Server initialization failed", e);
        }
    }

    private void startRequestProcessingThread() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !isShuttingDown.get()) {
                if (!requestQueue.isEmpty()) {
                    RequestTask task = requestQueue.poll();
                    if (task != null) {
                        requestProcessingPool.submit(() -> {
                            Response response = processRequest(task.request());
                            responseQueue.add(new ClientResponse(response, task.clientAddress()));
                            selector.wakeup();
                        });
                    }
                }
            }
        }).start();
    }

    private void startResponseHandlingThread() {
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && !isShuttingDown.get()) {
                if (!responseQueue.isEmpty()) {
                    ClientResponse clientResponse = responseQueue.peek();
                    if (clientResponse != null) {
                        responseSendingPool.submit(() -> {
                            try {
                                if (sendResponse(clientResponse)) {
                                    responseQueue.poll();
                                } else if (clientResponse.attemptCount() >= MAX_RESPONSE_ATTEMPTS) {
                                    log.warn("Max attempts reached for {}", clientResponse.address());
                                    responseQueue.poll();
                                } else {
                                    responseQueue.poll();
                                    responseQueue.add(clientResponse.withAttemptIncrement());
                                    log.warn("Attempt {}: Server responding timeout", clientResponse.attemptCount());
                                }
                            } catch (IOException e) {
                                log.error("Error sending response: {}", e.getMessage());
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private ByteBuffer getBuffer() {
        ByteBuffer buffer = bufferPool.poll();
        return buffer != null ? buffer : ByteBuffer.allocate(config.bufferSize());
    }

    private void releaseBuffer(ByteBuffer buffer) {
        if (buffer == null) return;
        buffer.clear();
        if (bufferPool.size() < MAX_BUFFER_POOL_SIZE) {
            bufferPool.offer(buffer);
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void run() {
        try {
            while (isRunning.get() || (isShuttingDown.get() && !responseQueue.isEmpty())) {
                int readyChannels = selector.select(isShuttingDown.get() ? 100 : 1000);
                if (readyChannels == 0) continue;
                processSelectedKeys();
            }
        } catch (IOException e) {
            if (!isShuttingDown.get()) {
                log.error("Server running error: {}", e.getMessage());
            } else {
                log.error("Server shutting down error: {}", e.getMessage());
            }
        } finally {
            close();
        }
    }

    private void processSelectedKeys() throws IOException {
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            keyIterator.remove();
            if (!key.isValid()) {
                log.warn("Invalid key encountered: {}", key);
                continue;
            }

            if (key.isReadable()) {
                requestReadingPool.submit(() -> {
                    try {
                        handleRequest(key);
                    } catch (IOException e) {
                        log.error("Error handling request: {}", e.getMessage());
                    }
                });
            }
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
                log.info("Received request from {}: {} - {}", clientAddress, request.command(), request);

                if (requestQueue.size() >= MAX_RESPONSE_QUEUE_SIZE) {
                    sendResponseImmediately(new Response("Server is busy, please try again later"), clientAddress);
                    log.warn("Max request queue size reached");
                } else {
                    requestQueue.add(new RequestTask(request, clientAddress));
                }
            }
        } finally {
            releaseBuffer(buffer);
        }
    }

    private Response processRequest(Request request) {
        return Router.getInstance().route(request);
    }

    private boolean sendResponse(ClientResponse clientResponse) throws IOException {
        ByteBuffer buffer = getBuffer();
        try {
            byte[] responseBytes = SerializationUtils.serialize(clientResponse.response());
            buffer.clear();
            buffer.put(responseBytes);
            buffer.flip();
            int bytesSent = channel.send(buffer, clientResponse.address());
            return bytesSent > 0;
        } finally {
            releaseBuffer(buffer);
        }
    }

    private boolean sendResponseImmediately(Response response, InetSocketAddress address) {
        ByteBuffer buffer = getBuffer();
        try {
            byte[] data = SerializationUtils.serialize(response);
            buffer.clear();
            buffer.put(data);
            buffer.flip();
            int bytesSent = channel.send(buffer, address);
            return bytesSent > 0;
        } catch (IOException e) {
            log.error("Immediate send failed", e);
        } finally {
            releaseBuffer(buffer);
        }
        return false;
    }

    public void stop() {
        if (isShuttingDown.compareAndSet(false, true)) {
            log.info("Initiating graceful shutdown...");
            isRunning.set(false);
            selector.wakeup();
        }
    }

    @Override
    public void close() {
        try {
            log.info("Flushing remaining responses: {}", responseQueue.size());
            flushRemainingResponses();
            log.info("Closing resources...");

            requestReadingPool.shutdownNow();
            requestProcessingPool.shutdownNow();
            responseSendingPool.shutdownNow();

            if (selector != null) {
                try {
                    if (selector.isOpen()) {
                        selector.keys().forEach(SelectionKey::cancel);
                        selector.close();
                    }
                } catch (IOException e) {
                    log.error("Error closing selector: {}", e.getMessage());
                }
            }
            if (channel != null) channel.close();
            log.info("Server stopped gracefully. Buffers in pool: {}", bufferPool.size());
        } catch (IOException e) {
            log.error("Error during shutdown: {}", e.getMessage());
        }
    }

    private void flushRemainingResponses() {
        if (selector == null || !selector.isOpen()) return;
        int flushedCount = 0;
        long startTime = System.currentTimeMillis();

        while (!responseQueue.isEmpty() && (System.currentTimeMillis() - startTime) < GRACEFUL_SHUTDOWN_TIMEOUT) {
            try {
                ClientResponse clientResponse = responseQueue.poll();
                if (clientResponse != null &&
                        sendResponseImmediately(clientResponse.response(), clientResponse.address())) {
                    flushedCount++;
                }
            } catch (Exception e) {
                log.warn("Failed to flush response: {}", e.getMessage());
            }
        }
        if (!responseQueue.isEmpty()) {
            log.warn("Failed to flush {} responses", responseQueue.size());
        }
        log.info("Flushed {} responses during shutdown", flushedCount);
    }

    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Received shutdown signal");
            stop();
        }));
    }

    private record ClientResponse(Response response, InetSocketAddress address, int attemptCount) {
        public ClientResponse(Response response, InetSocketAddress address) {
            this(response, address, 0);
        }

        public ClientResponse withAttemptIncrement() {
            return new ClientResponse(response, address, attemptCount + 1);
        }
    }

    private record RequestTask(Request request, InetSocketAddress clientAddress) {}
}