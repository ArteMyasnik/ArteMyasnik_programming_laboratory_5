package com.artemyasnik.command;

import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class Help extends Command {
    Help() {
        super("help", "display all available commands");
    }

    @Override
    public Response execute(Request request) {
        return null;
    }
}
