package com.artemyasnik.io.parser;

import com.artemyasnik.collection.classes.StudyGroup;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * Класс-обертка для списка StudyGroup.
 * Используется для сериализации и десериализации списка StudyGroup
 * с корневым элементом <StudyGroups>.
 */
@Setter
@Getter
@JacksonXmlRootElement(localName = "StudyGroups")
public final class StudyGroupWrapper {
    /**
     * -- GETTER --
     *  Возвращает список StudyGroup.
     *
     *
     * -- SETTER --
     *  Устанавливает список StudyGroup.
     *
     @return Список объектов StudyGroup.
      * @param studyGroups Список объектов StudyGroup.
     */
    @JacksonXmlElementWrapper(useWrapping = false) // Отключаем обертку для списка
    @JacksonXmlProperty(localName = "StudyGroup")
    private List<StudyGroup> studyGroups;

    /**
     * Конструктор по умолчанию (необходим для Jackson).
     */
    public StudyGroupWrapper() {
    }

    /**
     * Конструктор с параметром.
     *
     * @param studyGroups Список объектов StudyGroup.
     */
    public StudyGroupWrapper(List<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    /**
     * Возвращает строковое представление объекта StudyGroups.
     *
     * @return Строковое представление.
     */
    @Override
    public String toString() {
        return "StudyGroupWrapper{" +
                "studyGroups=" + studyGroups +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroupWrapper that = (StudyGroupWrapper) o;
        return Objects.equals(getStudyGroups(), that.getStudyGroups());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getStudyGroups());
    }
}