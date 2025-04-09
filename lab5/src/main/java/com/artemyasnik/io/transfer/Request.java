package com.artemyasnik.io.transfer;

import com.artemyasnik.collection.classes.StudyGroup;

import java.util.List;

public record Request(String command, List<String> args, List<StudyGroup> studyGroup) {
}
