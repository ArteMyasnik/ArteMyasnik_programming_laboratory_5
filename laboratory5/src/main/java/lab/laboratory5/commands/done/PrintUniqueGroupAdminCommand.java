package lab.laboratory5.commands.done;

import lab.laboratory5.entity.Person;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrintUniqueGroupAdminCommand implements Command {
    private final Receiver receiver;

    public PrintUniqueGroupAdminCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String execute(String... arguments) {
        List<StudyGroup> studyGroups = receiver.getAll();

        Set<Person> uniqueAdmins = new HashSet<>();

        for (StudyGroup studyGroup : studyGroups) {
            Person groupAdmin = studyGroup.getGroupAdmin();
            if (groupAdmin != null) {
                uniqueAdmins.add(groupAdmin);
            }
        }

        if (uniqueAdmins.isEmpty()) {
            return "No group admins found in the collection.";
        } else {
            StringBuilder result = new StringBuilder("Unique group admins in the collection:\n");
            for (Person admin : uniqueAdmins) {
                result.append(admin).append("\n");
            }
            return result.toString();
        }
    }
}