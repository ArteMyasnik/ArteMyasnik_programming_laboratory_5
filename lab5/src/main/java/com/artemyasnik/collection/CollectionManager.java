package com.artemyasnik.collection;

import com.artemyasnik.collection.classes.StudyGroup;
import com.artemyasnik.collection.id.IdGenerator;
import com.artemyasnik.collection.passport.PassportValidator;
import com.artemyasnik.db.dao.StudyGroupDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public final class CollectionManager {
    private static CollectionManager INSTANCE;
    private static final Logger log = LoggerFactory.getLogger(CollectionManager.class);
    private static final ReentrantLock lock = new ReentrantLock();
    private final List<StudyGroup> collection;
    private final LocalDateTime initializationDate;
    private final StudyGroupDAO studyGroupDAO;
    private final PassportValidator passportValidator;

    private CollectionManager() {
        this.collection = new LinkedList<>();
        this.initializationDate = LocalDateTime.now();
        this.studyGroupDAO = StudyGroupDAO.getInstance();
        this.passportValidator = PassportValidator.getInstance();
        loadFromDatabase();
    }

    public static CollectionManager getInstance() {
        lock.lock();
        try {
            if (INSTANCE == null) {
                INSTANCE = new CollectionManager();
            }
            return INSTANCE;
        } finally {
            lock.unlock();
        }
    }

    private void loadFromDatabase() {
        lock.lock();
        try {
            collection.clear();
            List<StudyGroup> loadedGroups = studyGroupDAO.findAllWithPerson();

            int maxId = loadedGroups.stream()
                    .mapToInt(StudyGroup::getId)
                    .max()
                    .orElse(0);
            IdGenerator.getInstance().initializeWith(maxId + 1);

            collection.addAll(loadedGroups);

            loadedGroups.stream()
                    .filter(g -> g.getGroupAdmin() != null)
                    .forEach(g -> passportValidator.validate(g.getGroupAdmin().getPassportID()));

            log.info("Loaded {} study groups from database", loadedGroups.size());
        } catch (SQLException e) {
            log.error("Failed to load collection from database", e);
            collection.clear();
        } finally {
            lock.unlock();
        }
    }

    public String add(StudyGroup studyGroup, int ownerId) {
        lock.lock();
        try {
            int newId = studyGroupDAO.saveAndReturnId(studyGroup, ownerId);
            studyGroup.setId(newId);
            collection.add(studyGroup);
            if (studyGroup.getGroupAdmin() != null) {
                passportValidator.validate(studyGroup.getGroupAdmin().getPassportID());
            }

            return "StudyGroup added successfully with ID: " + newId;
        } catch (SQLException e) {
            log.error("Failed to add StudyGroup", e);
            return "Failed to add StudyGroup: " + e.getMessage();
        } finally {
            lock.unlock();
        }
    }

    public String update(int id, StudyGroup updatedStudyGroup, int ownerId) {
        lock.lock();
        try {
            Optional<StudyGroup> existing = collection.stream()
                    .filter(g -> g.getId() == id)
                    .findFirst();
            if (existing.isEmpty()) { return "StudyGroup with ID " + id + " not found"; }
            studyGroupDAO.updateInDatabase(updatedStudyGroup, ownerId);

            StudyGroup oldGroup = existing.get();
            if (oldGroup.getGroupAdmin() != null) {
                passportValidator.remove(oldGroup.getGroupAdmin().getPassportID());
            }

            collection.remove(oldGroup);
            updatedStudyGroup.setId(id);
            collection.add(updatedStudyGroup);

            if (updatedStudyGroup.getGroupAdmin() != null) {
                passportValidator.validate(updatedStudyGroup.getGroupAdmin().getPassportID());
            }

            return "StudyGroup with ID " + id + " updated successfully";
        } catch (SQLException e) {
            log.error("Failed to update StudyGroup", e);
            return "Failed to update StudyGroup: " + e.getMessage();
        } finally {
            lock.unlock();
        }
    }

    public String removeById(int id, int ownerId) {
        lock.lock();
        try {
            Optional<StudyGroup> groupToRemove = collection.stream()
                    .filter(g -> g.getId() == id)
                    .findFirst();

            if (!groupToRemove.isPresent()) {
                return "Error: StudyGroup with ID " + id + " not found";
            }
            StudyGroup removedGroup = groupToRemove.get();
            if (!studyGroupDAO.isOwner(id, ownerId)) {
                return "Error: You don't have permission to remove this study group";
            }
            studyGroupDAO.removeFromDatabase(id, ownerId);
            if (removedGroup.getGroupAdmin() != null) {
                passportValidator.remove(removedGroup.getGroupAdmin().getPassportID());
            }
            collection.remove(removedGroup);
            return "Element with id " + id + " was successfully removed";
        } catch (SQLException e) {
            log.error("Failed to remove StudyGroup", e);
            return "Error: Failed to remove StudyGroup - " + e.getMessage();
        } finally {
            lock.unlock();
        }
    }

    public String clear(int ownerId) {
        lock.lock();
        try {
            List<StudyGroup> ownerGroups = collection.stream()
                    .filter(g -> {
                        try {
                            return studyGroupDAO.isOwner(g.getId(), ownerId);
                        } catch (SQLException e) {
                            log.error("Error checking ownership", e);
                            return false;
                        }
                    })
                    .toList();

            for (StudyGroup group : ownerGroups) {
                studyGroupDAO.removeFromDatabase(group.getId(), ownerId);
                if (group.getGroupAdmin() != null) {
                    passportValidator.remove(group.getGroupAdmin().getPassportID());
                }
            }

            collection.removeAll(ownerGroups);

            return "Removed " + ownerGroups.size() + " study groups";
        } catch (SQLException e) {
            log.error("Failed to clear collection", e);
            return "Failed to clear collection: " + e.getMessage();
        } finally {
            lock.unlock();
        }
    }

    public String removeHead(int ownerId) {
        lock.lock();
        try {
            if (collection.isEmpty()) {
                return "Collection is empty";
            }

            StudyGroup head = collection.get(0);
            return this.removeById(head.getId(), ownerId);
        } finally {
            lock.unlock();
        }
    }

    public String show() {
        lock.lock();
        try {
            if (collection.isEmpty()) {
                return "Collection is empty";
            }

            StringBuilder sb = new StringBuilder();
            collection.stream()
                    .sorted()
                    .forEach(group -> sb.append(group).append("\n"));

            return sb.toString();
        } finally {
            lock.unlock();
        }
    }

    public String info() {
        lock.lock();
        try {
            return "Type: " + collection.getClass().getSimpleName() + "\n" +
                    "Initialization date: " + initializationDate + "\n" +
                    "Number of elements: " + collection.size();
        } finally {
            lock.unlock();
        }
    }

    public List<StudyGroup> getCollection() {
        lock.lock();
        try {
            return new ArrayList<>(collection);
        } finally {
            lock.unlock();
        }
    }

    public String save() {
        return "Data is automatically persisted to database";
    }
}