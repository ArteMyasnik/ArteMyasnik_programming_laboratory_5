package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class RemoveHead extends Command {
    RemoveHead() {
        super("remove_head", "remove first element from collection");
    }

    @Override
    public Response execute(Request request) {
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty", request.userDTO());
        }
        return new Response(CollectionManager.getInstance().removeHead(request.userDTO().id()), request.userDTO());
    }
}
