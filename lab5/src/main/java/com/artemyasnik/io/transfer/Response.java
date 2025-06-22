package com.artemyasnik.io.transfer;

import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.db.dto.UserDTO;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public record Response(String message, List<StudyGroup> studyGroup, String script, UserDTO userDTO) implements Serializable {
    public static Response empty(UserDTO userDTO) {
        return new Response(null, Collections.emptyList(), null, userDTO);
    }

    public Response(final String message, UserDTO userDTO) {
        this(message, Collections.emptyList(), null, userDTO);
    }

    public Response(final String message, final List<StudyGroup> studyGroup, UserDTO userDTO) {
        this(message, studyGroup, null, userDTO);
    }
}
