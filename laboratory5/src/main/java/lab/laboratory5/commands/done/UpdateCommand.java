package lab.laboratory5.commands.done;

import lab.laboratory5.commands.utils.ElementBuilder;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

public class UpdateCommand implements Command {
    private final Receiver receiver;
    private final ElementBuilder elementBuilder;

    public UpdateCommand(Receiver receiver, ElementBuilder elementBuilder) {
        this.receiver = receiver;
        this.elementBuilder = elementBuilder;
    }

    @Override
    public String execute(String... arguments) {
        if (arguments.length == 0) {
            return "Error: ID is required.";
        }

        try {
            int id = Integer.parseInt(arguments[0]);
            StudyGroup existingGroup = receiver.getById(id);
            if (existingGroup == null) {
                return "Error: Study group with ID " + id + " not found.";
            }
            String oldPassportID = existingGroup.getGroupAdmin().getPassportID();
            elementBuilder.getPassportValidator().removePassword(oldPassportID);
            StudyGroup updatedGroup = elementBuilder.createStudyGroup();
            receiver.update(id, updatedGroup);
            return "Study group with ID " + id + " updated successfully!";
        } catch (NumberFormatException e) {
            return "Error: Invalid ID format.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
}