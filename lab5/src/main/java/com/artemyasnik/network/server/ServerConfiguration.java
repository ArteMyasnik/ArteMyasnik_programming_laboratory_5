package com.artemyasnik.network.server;

public record ServerConfiguration(int port, int bufferSize) {
    public ServerConfiguration {
        if (port <= 0 || port > 65535) throw new IllegalArgumentException("Port must be in the range 1-65535");

        if (bufferSize <= 0) throw new IllegalArgumentException("Buffer size must be greater than 0");
    }

    @Override
    public String toString() {
        return "ServerConfiguration{" +
                "port=" + port +
                ", bufferSize=" + bufferSize +
                '}';
    }
}
