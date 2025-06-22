package com.artemyasnik.command;

import com.artemyasnik.collection.CollectionManager;
import com.artemyasnik.collection.classes.FormOfEducation;
import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FilterLessThanFormOfEducation extends Command {
    FilterLessThanFormOfEducation() {
        super("filter_less_than_form_of_education", new String[]{"formOfEducation"} , "display elements whose \"formOfEducation\" field value is less than the specified value");
    }

    @Override
    public Response execute(Request request) {
        if (request.args() == null) {
            return new Response("Type the FormOfEducation argument", request.userDTO());
        }
        if (CollectionManager.getInstance().getCollection().isEmpty()) {
            return new Response("Collection is empty", request.userDTO());
        }
        FormOfEducation targetForm;
        try {
            targetForm = FormOfEducation.valueOf(request.args().get(0));
        } catch (IllegalArgumentException e) {
            return new Response("Error: Invalid formOfEducation. Available values: " +
                    Arrays.stream(FormOfEducation.values())
                            .map(Enum::name)
                            .collect(Collectors.joining(", ")), request.userDTO());
        }

        List<StudyGroup> filtered = CollectionManager.getInstance().getCollection().stream()
                .filter(group -> group.getFormOfEducation() != null)
                .filter(group -> group.getFormOfEducation().ordinal() < targetForm.ordinal())
                .toList();

        if (filtered.isEmpty()) {
            return new Response("No elements with formOfEducation less than %s%n".formatted(targetForm), request.userDTO());
        }
        String result = filtered.stream()
                .map(StudyGroup::toString)
                .collect(Collectors.joining((System.lineSeparator() + System.lineSeparator())));
        return new Response(result, request.userDTO());
    }
}
