package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class UpdateId extends Command {
    UpdateId() {
        super("update_id", new String[]{"id"}, "update element by id", 1);
    }

    @Override
    public Response execute(Request request) {
        if (request.args() == null) {
            return new Response("Type the id argument");
        }
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty");
        }
        if (request.studyGroup() == null || request.studyGroup().isEmpty()) {
            return new Response("No study group to update");
        }
        if (CollectionManager.getInstance().removeById(Integer.valueOf(request.args().get(0)), request.userDTO().id())
                .equalsIgnoreCase("Element with id " + Integer.valueOf(request.args().get(0)) + " was successfully removed")) {
            CollectionManager.getInstance().getCollection().add(request.studyGroup().get(0));
            return new Response("Study group updated successfully");
        } else {
            return new Response(CollectionManager.getInstance().removeById(Integer.valueOf(request.args().get(0)), request.userDTO().id()));
        }
    }
}
