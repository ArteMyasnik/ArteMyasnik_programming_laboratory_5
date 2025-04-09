package com.artemyasnik.command;

import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class Exit extends Command {
    Exit() {
        super("exit", "exits the program");
    }

    @Override
    public Response execute(Request request) {
        System.exit(0);
        return new Response("Exiting...");
    }
}
