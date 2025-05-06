package com.artemyasnik.network.client;

public record ClientConfiguration(String host, int port, int bufferSize) {
    public ClientConfiguration {
        if (host == null || host.isBlank()) throw new IllegalArgumentException("Host can't be null or blank");

        if (port <= 0 || port > 65535) throw new IllegalArgumentException("Port must be in the range 1-65535");

        if (bufferSize <= 0) throw new IllegalArgumentException("Buffer size must be greater than 0");
    }

    @Override
    public String toString() {
        return "ClientConfiguration{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", bufferSize=" + bufferSize +
                '}';
    }
}
