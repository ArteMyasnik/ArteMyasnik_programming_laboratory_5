package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class Show extends Command {
    Show() {
        super("show", "displays the entire collection");
    }

    @Override
    public Response execute(Request request) {
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty");
        }
        return new Response(CollectionManager.getInstance().show());
    }
}
