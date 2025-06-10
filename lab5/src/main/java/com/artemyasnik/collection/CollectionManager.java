package com.artemyasnik.collection;

import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.collection.id.IdGenerator;
import com.artemyasnik.collection.passport.PassportValidator;
import com.artemyasnik.io.file.MyInputStreamReader;
import com.artemyasnik.io.file.MyFileWriter;
import com.artemyasnik.io.configuration.FileConfiguration;
import com.artemyasnik.io.parser.XmlReader;
import com.artemyasnik.io.parser.XmlWriter;
import com.artemyasnik.io.transfer.Response;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Getter
public final class CollectionManager {
    private static CollectionManager INSTANCE;
    private final static Logger log = LoggerFactory.getLogger(CollectionManager.class);
    private final List<StudyGroup> collection = new LinkedList<>();
    private final java.time.LocalDateTime initializationDate;

    private CollectionManager() {
        this.initializationDate = java.time.LocalDateTime.now();
        load();
    }

    public static CollectionManager getInstance() {
        return INSTANCE == null ? INSTANCE = new CollectionManager() : INSTANCE;
    }

    private void load() {
        try {
            collection.clear();
            MyInputStreamReader myInputStreamReader = new MyInputStreamReader(FileConfiguration.DATA_FILE_PATH);
            XmlReader xmlReader = new XmlReader();
            List<StudyGroup> loadedGroups = xmlReader.parseXml(myInputStreamReader.readFile());

            int maxId = loadedGroups.stream()
                    .mapToInt(StudyGroup::getId)
                    .max()
                    .orElse(1);
            if (loadedGroups.stream().anyMatch(g -> g.getId() == null || g.getId() < 1)) {
                throw new IllegalArgumentException("Collection contains invalid IDs");
            }

            IdGenerator idGenerator = IdGenerator.getInstance();
            idGenerator.initializeWith(maxId);

            if (Files.size(FileConfiguration.ID_SEQ_FILE_PATH) == 0) {
                idGenerator.saveLastId(maxId);
                log.debug("Initialized ID sequence file with max ID: {}", maxId);
            }

            collection.addAll(loadedGroups);
            log.info("Collection loaded successfully. Max ID: {}", maxId);
//            return "Collection was loaded successfully";
        } catch (IOException | IllegalArgumentException e) {
            collection.clear();
            log.error("Collection load failed: {}", e.getMessage());
            try {
                IdGenerator.getInstance().initializeWith(1);
                log.debug("Initialized ID generator with default value 1");
            } catch (Exception ex) {
                log.error("Failed to initialize ID generator: {}", ex.getMessage());
                //            return "Collection load failed";
            }
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
        collection.stream()
                .sorted(StudyGroup::compareTo)
                .forEach(group -> sb.append(group.toString()).append(System.lineSeparator()));
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
