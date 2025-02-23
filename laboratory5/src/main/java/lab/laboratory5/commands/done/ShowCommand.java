package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;
import lab.laboratory5.entity.StudyGroup;

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
            System.out.println("Study groups in the collection:");
            for (StudyGroup studyGroup : receiver.getAll()) {
                System.out.println(studyGroup);
            }
        }
    }
}