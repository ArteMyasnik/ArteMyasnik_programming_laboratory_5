package com.artemyasnik.io.transfer;

import com.artemyasnik.collection.classes.StudyGroup;

import java.util.Collections;
import java.util.List;

public record Response(String message, List<StudyGroup> studyGroup, String script) {
    public static Response empty() {
        return new Response(null, Collections.emptyList(), null);
    }

    public Response(final String message) {
        this(message, Collections.emptyList(), null);
    }

    public Response(final String message, final List<StudyGroup> studyGroup) {
        this(message, studyGroup, null);
    }
}
