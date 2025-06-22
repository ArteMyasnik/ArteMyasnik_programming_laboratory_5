package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class Add extends Command {
    public Add() {
        super("add", Command.EMPTY_ARGS,  "add the study group in the collection", 1);
    }

    @Override
    public Response execute(Request request) {
        if (request.studyGroup() == null || request.studyGroup().isEmpty()) {
            return new Response("No study group to add", request.userDTO());
        }
        CollectionManager.getInstance().getCollection().add(request.studyGroup().get(0));
        return new Response("Study group added", request.userDTO());
    }
}
