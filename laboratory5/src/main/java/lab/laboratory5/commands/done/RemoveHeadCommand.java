package lab.laboratory5.commands.done;

import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

public class RemoveHeadCommand implements Command {
    private final Receiver receiver;
    private final PassportValidator passportValidator;

    public RemoveHeadCommand(Receiver receiver, PassportValidator passportValidator) {
        this.receiver = receiver;
        this.passportValidator = passportValidator;
    }

    @Override
    public String execute(String... arguments) {
        try {
            StudyGroup removedGroup = receiver.removeHead();

            String passportID = removedGroup.getGroupAdmin().getPassportID();
            passportValidator.removePassword(passportID);

            return "Removed study group:\n" + removedGroup;
        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        }
    }
}