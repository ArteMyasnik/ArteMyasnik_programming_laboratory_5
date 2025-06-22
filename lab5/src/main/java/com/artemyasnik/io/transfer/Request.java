package com.artemyasnik.io.transfer;

import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.db.dto.UserDTO;

import java.io.Serializable;
import java.util.List;

public record Request(String command, List<String> args, List<StudyGroup> studyGroup, UserDTO userDTO) implements Serializable {}
