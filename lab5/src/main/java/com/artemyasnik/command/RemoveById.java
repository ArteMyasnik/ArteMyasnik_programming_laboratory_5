package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class RemoveById extends Command {
    RemoveById() {
        super("remove_by_id", new String[]{"id"}, "removes element by id");
    }

    @Override
    public Response execute(Request request) {
        if (request.args() == null) {
            return new Response("Type the id argument", request.userDTO());
        }
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty", request.userDTO());
        }
        return new Response(CollectionManager.getInstance().removeById(Integer.valueOf(request.args().get(0)), request.userDTO().id()), request.userDTO());
    }
}
