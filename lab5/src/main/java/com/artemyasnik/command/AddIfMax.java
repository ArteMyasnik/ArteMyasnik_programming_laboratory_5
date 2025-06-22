package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

import java.util.Collections;

public final class AddIfMax extends Command {
     AddIfMax() {
         super("add_if_max", Command.EMPTY_ARGS, "add element in collection if it is max", 1);
     }

    @Override
    public Response execute(Request request) {
        if (request.studyGroup() == null || request.studyGroup().isEmpty()) {
            return new Response("No study group to add", request.userDTO());
        }
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            CollectionManager.getInstance().getCollection().add(request.studyGroup().get(0));
            return new Response("Study group added", request.userDTO());
        }

        if (request.studyGroup().get(0).compareTo(Collections.max(CollectionManager.getInstance().getCollection())) > 0) {
            CollectionManager.getInstance().getCollection().add(request.studyGroup().get(0));
            return new Response("Study group added", request.userDTO());
        } else {
            return new Response("Study group not added: not the max element", request.userDTO());
        }
    }
}
