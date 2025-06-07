package com.artemyasnik.collection;

import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.collection.passport.PassportValidator;
import com.artemyasnik.io.file.MyInputStreamReader;
import com.artemyasnik.io.file.MyFileWriter;
import com.artemyasnik.io.configuration.FileConfiguration;
import com.artemyasnik.io.parser.XmlReader;
import com.artemyasnik.io.parser.XmlWriter;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

@Getter
public final class CollectionManager {
    private static CollectionManager instance;
    private final List<StudyGroup> collection = new LinkedList<>();
    private final java.time.LocalDateTime initializationDate;

    private CollectionManager() {
        this.initializationDate = java.time.LocalDateTime.now();
        load();
    }

    public static CollectionManager getInstance() {
        return instance == null ? instance = new CollectionManager() : instance;
    }

    private void load() {
        try {
            collection.clear();
            MyInputStreamReader myInputStreamReader = new MyInputStreamReader(FileConfiguration.DATA_FILE_PATH);
            XmlReader xmlReader = new XmlReader();
            collection.addAll(xmlReader.parseXml(myInputStreamReader.readFile()));
//            return "Collection was loaded successfully";
        } catch (IOException e) {
            System.err.println(e.getMessage());
//            return "Collection load failed";
        }
    }

    public String save() {
        try {
            XmlWriter xmlWriter = new XmlWriter();
            String xmlContent = xmlWriter.serializeToXml(collection);
            MyFileWriter myFileWriter = new MyFileWriter(FileConfiguration.DATA_FILE_PATH);
            myFileWriter.writeFile(xmlContent);
            return "Collection was saved successfully";
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return "Collection save failed";
        }
    }

    public String clear() {
        collection.clear();
        PassportValidator.getInstance().clear();
        return "Collection was cleared successfully";
    }

    public String show() {
        StringBuilder sb = new StringBuilder();
        for (StudyGroup group : collection) {
            sb.append(group.toString()).append(System.lineSeparator());
        }
        return "Collection: " + sb;
    }

    public String info() {
        return "Collection type: " + collection.getClass().getSimpleName() + "\n" +
                "Initialization date: " + initializationDate + "\n" +
                "Number of elements: " + collection.size();
    }

    public String update(Integer id, StudyGroup updatedStudyGroup) {
        if (updatedStudyGroup == null) {
            throw new IllegalArgumentException("Updated group can't be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Id can't be null");
        }
        Iterator<StudyGroup> iterator = collection.iterator();
        while (iterator.hasNext()) {
            StudyGroup group = iterator.next();
            if (group.getId().equals(id)) {
                if (!group.getGroupAdmin().getPassportID().equals(updatedStudyGroup.getGroupAdmin().getPassportID())) {
                    updatedStudyGroup.setId(id);
                    PassportValidator.getInstance().remove(group.getGroupAdmin().getPassportID());
                    iterator.remove();
                    PassportValidator.getInstance().validate(updatedStudyGroup.getGroupAdmin().getPassportID());
                    collection.add(updatedStudyGroup);
                } else {
                    updatedStudyGroup.setId(id);
                    iterator.remove();
                    collection.add(updatedStudyGroup);
                }
                return "Collection was updated";
            }
        }
        return "Collection update failed";
    }

    public String remove(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Collection remove failed. Id can't be null");
        }
        if (collection.isEmpty()) {
            return "Collection remove failed. Collection is empty, nothing to remove";
        }

        Iterator<StudyGroup> iterator = collection.iterator();
        while (iterator.hasNext()) {
            StudyGroup group = iterator.next();
            if (id.equals(group.getId())) {
                PassportValidator.getInstance().remove(group.getGroupAdmin().getPassportID());
                iterator.remove();
                return "Element with id " + id + " was successfully removed";
            }
        }
        return "Collection remove failed. Element with id " + id + " not found in collection";
    }

    public String remove_head() {
        try {
            this.remove(0);
            return "Element head was successfuly removed";
        } catch (Exception e) {
            return "Collection remove_head failed";
        }
    }
}
