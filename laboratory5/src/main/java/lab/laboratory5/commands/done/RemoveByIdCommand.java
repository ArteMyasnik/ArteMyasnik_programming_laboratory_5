package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;
import lab.laboratory5.commands.utils.PassportValidator;
import lab.laboratory5.entity.StudyGroup;

public class RemoveByIdCommand implements Command {
    private final Receiver receiver;
    private final PassportValidator passportValidator;

    public RemoveByIdCommand(Receiver receiver, PassportValidator passportValidator) {
        this.receiver = receiver;
        this.passportValidator = passportValidator;
    }

    @Override
    public String execute(String... arguments) {
        if (arguments.length == 0) {
            return "Error: ID is required.";
            }

        try {
            int id = Integer.parseInt(arguments[0]);
            StudyGroup removedGroup = receiver.removeById(id);
            String passportID = removedGroup.getGroupAdmin().getPassportID();
            passportValidator.removePassword(passportID);
            return "Study group with ID " + id + " removed successfully!";
        } catch (NumberFormatException e) {
            return "Error: Invalid ID format.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
}