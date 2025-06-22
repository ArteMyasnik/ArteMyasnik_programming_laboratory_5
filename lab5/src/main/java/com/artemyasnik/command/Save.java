package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class Save extends Command {
    Save() {
        super("save", "save collection to file");
    }

    @Override
    public Response execute(Request request) {
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty", request.userDTO());
        }
        return new Response(CollectionManager.getInstance().save(), request.userDTO());
    }
}
