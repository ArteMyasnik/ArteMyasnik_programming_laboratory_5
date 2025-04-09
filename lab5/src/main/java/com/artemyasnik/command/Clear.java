package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class Clear extends Command {
    Clear() {
        super("clear", "clear the collection");
    }

    @Override
    public Response execute(Request request) {
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty");
        }
        return new Response(CollectionManager.getInstance().clear());
    }
}
