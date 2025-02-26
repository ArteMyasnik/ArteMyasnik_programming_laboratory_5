package lab.laboratory5;

import lab.laboratory5.entity.StudyGroup;
import lombok.Getter;

import java.time.LocalDate;
import java.util.LinkedList;

public class Receiver {
    private final LinkedList<StudyGroup> collection = new LinkedList<>();
    @Getter
    private final LocalDate initializationDate = LocalDate.now();

    public void add(StudyGroup studyGroup) {
        collection.add(studyGroup);
    }

    public LinkedList<StudyGroup> getAll() {
        return new LinkedList<>(collection);
    }

    public void clear() {
        collection.clear();
    }

    public int size() {
        return collection.size();
    }

    public String getCollectionType() {
        return collection.getClass().getSimpleName();
    }

    public StudyGroup getById(int id) {
        for (StudyGroup studyGroup : collection) {
            if (studyGroup.getId() == id) {
                return studyGroup;
            }
        }
        return null;
    }

    public void update(int id, StudyGroup updatedGroup) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).getId() == id) {
                collection.set(i, updatedGroup);
                return;
            }
        }
        throw new IllegalArgumentException("Study group with ID " + id + " not found.");
    }

    public void removeById(int id) {
        collection.removeIf(studyGroup -> studyGroup.getId() == id);
    }

    public StudyGroup removeHead() {
        if (collection.isEmpty()) {
            throw new IllegalStateException("The collection is empty.");
        }
        return collection.removeFirst();
    }
}