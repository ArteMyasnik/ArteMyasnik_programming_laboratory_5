package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;
import lab.laboratory5.entity.StudyGroup;

import java.util.Comparator;
import java.util.List;

public class ShowCommand implements Command {
    private final Receiver receiver;

    public ShowCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String... arguments) {
        if (receiver.size() == 0) {
            System.out.println("The collection is empty.");
        } else {
            List<StudyGroup> studyGroups = receiver.getAll();

            studyGroups.sort(Comparator.comparing(
                    StudyGroup::getName,
                    String.CASE_INSENSITIVE_ORDER
            ));

            System.out.println("Study groups in the collection (sorted by name):");
            for (StudyGroup studyGroup : studyGroups) {
                System.out.println(studyGroup);
            }
        }
    }
}