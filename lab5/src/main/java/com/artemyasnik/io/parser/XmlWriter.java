package com.artemyasnik.io.parser;

import com.artemyasnik.collection.classes.StudyGroup;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class XmlWriter {

    private static final XmlMapper xmlMapper = new XmlMapper();
    private Path filePath;

    static {
        xmlMapper.registerModule(new JavaTimeModule()); // Для поддержки ZonedDateTime
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT); // Для форматирования
    }

    public XmlWriter(final Path filePath) {
        this.filePath = filePath;
    }

    public XmlWriter() {
    }

    /**
     * Записывает список StudyGroup в XML-файл с корневым элементом <StudyGroupWrapper>.
     *
     * @param studyGroups Список объектов StudyGroup для записи.
     * @param filePath    Путь к файлу, в который будет записан XML.
     */
    public static void writeToFile(List<StudyGroup> studyGroups, String filePath) {
        try {
            StudyGroupWrapper wrapper = new StudyGroupWrapper(studyGroups); // Обертка для списка
            xmlMapper.writeValue(new File(filePath), wrapper);
            System.out.println("Список StudyGroup успешно записан в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    /**
     * Записывает один объект StudyGroup в XML-файл.
     *
     * @param studyGroup Объект StudyGroup для записи.
     * @param filePath   Путь к файлу, в который будет записан XML.
     */
    public static void writeToFile(StudyGroup studyGroup, String filePath) {
        try {
            xmlMapper.writeValue(new File(filePath), studyGroup);
            System.out.println("StudyGroup успешно записан в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }

    /**
     * Преобразует один объект StudyGroup в XML-строку.
     *
     * @param studyGroup Объект StudyGroup.
     * @return XML-строка.
     * @throws IOException Если произошла ошибка при преобразовании.
     */
    public String serializeToXml(StudyGroup studyGroup) throws IOException {
        return xmlMapper.writeValueAsString(studyGroup);
    }

    /**
     * Преобразует список StudyGroup в XML-строку.
     *
     * @param studyGroups Список объектов StudyGroup.
     * @return XML-строка.
     * @throws IOException Если произошла ошибка при преобразовании.
     */
    public String serializeToXml(List<StudyGroup> studyGroups) throws IOException {
        StudyGroupWrapper wrapper = new StudyGroupWrapper(studyGroups);
        return xmlMapper.writeValueAsString(wrapper);
    }
}
