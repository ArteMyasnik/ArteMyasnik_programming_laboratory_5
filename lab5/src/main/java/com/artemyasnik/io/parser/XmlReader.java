package com.artemyasnik.io.parser;

import com.artemyasnik.collection.classes.StudyGroup;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public final class XmlReader {

    private static final XmlMapper xmlMapper = new XmlMapper();
    private Path filePath;

    static {
        xmlMapper.registerModule(new JavaTimeModule()); // Для поддержки ZonedDateTime
    }

    public XmlReader(final Path filePath) {
        this.filePath = filePath;
    }

    public XmlReader() {
    }

    public List<StudyGroup> parseXml(String xmlString) throws IOException {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            return Collections.emptyList();
        }
        StudyGroupWrapper wrapper = xmlMapper.readValue(xmlString, StudyGroupWrapper.class);
        return wrapper.getStudyGroups();
    }

    public StudyGroup parseSingleStudyGroup(String xmlString) throws IOException {
        return xmlMapper.readValue(xmlString, StudyGroup.class);
    }

    public List<StudyGroup> readListFromFile() {
        try {
            StudyGroupWrapper wrapper = xmlMapper.readValue(new File(String.valueOf(this.filePath)), StudyGroupWrapper.class);
            return wrapper.getStudyGroups();
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return null;
        }
    }

    public StudyGroup readFromFile() {
        try {
            return xmlMapper.readValue(new File(String.valueOf(this.filePath)), StudyGroup.class);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return null;
        }
    }
}