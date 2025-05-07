package com.artemyasnik.network.client;

import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import com.artemyasnik.io.workers.console.ConsoleWorker;

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
import java.util.List;

public class Client implements Runnable {
    private final ClientConfiguration config;
    private final ConsoleWorker console;
    private DatagramChannel channel;
    private Selector selector;

    public Client(ClientConfiguration config, ConsoleWorker console) {
        this.config = config;
        this.console = console;
    }

    @Override
    public void run() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(config.host(), config.port()));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            console.writeln("Клиент запущен с конфигурацией: " + config);

            ByteBuffer buffer = ByteBuffer.allocate(config.bufferSize());

            while (true) {
                String command = console.read("Введите команду (или 'exit' для выхода): ");
                if ("exit".equalsIgnoreCase(command)) {
                    break;
                }

                Request request = new Request(command, List.of(), List.of());
                sendRequest(request, buffer);

                Response response = receiveResponse(buffer);
                if (response != null) {
                    console.writeln("Ответ сервера: " + response.message());
                }
            }
        } catch (IOException e) {
            console.writeln("Ошибка клиента: " + e.getMessage());
        } finally {
            closeResources();
        }
    }

    private void sendRequest(Request request, ByteBuffer buffer) throws IOException {
        byte[] requestBytes = serializeRequest(request);
        buffer.clear();
        buffer.put(requestBytes);
        buffer.flip();
        channel.write(buffer);
    }

    private Response receiveResponse(ByteBuffer buffer) throws IOException {
        int attempts = 3;
        while (attempts-- > 0) {
            int ready = selector.select(3000); // Таймаут 3 секунды
            if (ready > 0) {
                var keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (key.isReadable()) {
                        buffer.clear();
                        channel.read(buffer);
                        buffer.flip();
                        try {
                            return deserializeResponse(buffer);
                        } catch (ClassNotFoundException e) {
                            console.writeln("Ошибка десериализации ответа: " + e.getMessage());
                        }
                    }
                }
            } else {
                console.writeln("Таймаут ожидания ответа. Осталось попыток: " + attempts);
            }
        }
        return null;
    }

    private byte[] serializeRequest(Request request) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
            return baos.toByteArray();
        }
    }

    private Response deserializeResponse(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Response) ois.readObject();
        }
    }

    private void closeResources() {
        try {
            if (selector != null) selector.close();
            if (channel != null) channel.close();
            console.writeln("Клиент остановлен");
        } catch (IOException e) {
            console.writeln("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}