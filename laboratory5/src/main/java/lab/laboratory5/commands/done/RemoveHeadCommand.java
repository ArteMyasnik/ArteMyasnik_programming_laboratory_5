package lab.laboratory5.commands.done;

import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

public class RemoveHeadCommand implements Command {
    private final Receiver receiver;

    public RemoveHeadCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String execute(String... arguments) {
        try {
            StudyGroup removedGroup = receiver.removeHead();
            return "Removed study group:\n" + removedGroup;
        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        }
    }
}
