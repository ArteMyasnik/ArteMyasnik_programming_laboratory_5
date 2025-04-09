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
        CollectionManager.getInstance().remove(Integer.valueOf(request.args().get(0)));
        CollectionManager.getInstance().getCollection().add(request.studyGroup().get(0));
        return new Response("Study group updated successfully");
    }
}
