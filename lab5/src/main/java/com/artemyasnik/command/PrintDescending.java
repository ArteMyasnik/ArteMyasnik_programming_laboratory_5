package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

import java.util.Comparator;
import java.util.stream.Collectors;

public final class PrintDescending extends Command {
    PrintDescending() {
        super("print_descending", "displays the collection elements in descending order");
    }

    @Override
    public Response execute(Request request) {
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty");
        }
        return new Response("%s".formatted(
                CollectionManager.getInstance().getCollection().stream()
                        .sorted(Comparator.reverseOrder())
                        .map(StudyGroup::toString)
                        .collect(Collectors.joining(System.lineSeparator()))
        ));
    }
}
