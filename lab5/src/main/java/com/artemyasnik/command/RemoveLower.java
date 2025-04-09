package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

public final class RemoveLower extends Command {
    RemoveLower() {
        super("remove_lower", Command.EMPTY_ARGS, "remove all elements in collection that are lower than element", 1);
    }

    @Override
    public Response execute(Request request) {
        if (request.studyGroup() == null || request.studyGroup().isEmpty()) {
            return new Response("No study group to compare.");
        }
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty");
        }
        CollectionManager.getInstance().getCollection().removeIf(studyGroup -> studyGroup.compareTo(request.studyGroup().get(0)) < 0);
        return new Response("Study groups that are lower, than the given element successfully removed");
    }
}
