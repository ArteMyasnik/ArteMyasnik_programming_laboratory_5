package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.collection.classes.Person;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

import java.util.List;
import java.util.Objects;

public final class PrintUniqueGroupAdmin extends Command {
    PrintUniqueGroupAdmin() {
        super("print_unique_group_admin", "displays the unique values of the \"groupAdmin\" field of all elements in the collection");
    }

    @Override
    public Response execute(Request request) {
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty", request.userDTO());
        }
        List<Person> uniqueAdmins = CollectionManager.getInstance().getCollection().stream()
                .map(StudyGroup::getGroupAdmin)
                .distinct()
                .filter(Objects::nonNull)
                .toList();
        return new Response(uniqueAdmins.toString(), request.userDTO());
    }
}
