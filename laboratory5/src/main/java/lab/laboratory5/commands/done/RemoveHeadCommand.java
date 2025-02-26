package lab.laboratory5.commands.done;

import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

public class RemoveHeadCommand implements Command {
    private final Receiver receiver;

    public RemoveHeadCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String... arguments) {
        try {
            StudyGroup removedGroup = receiver.removeHead();
            System.out.println("Removed study group:");
            System.out.println(removedGroup);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
