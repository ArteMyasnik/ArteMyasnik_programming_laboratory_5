package lab.laboratory5.commands.done;

import lab.laboratory5.commands.utils.ElementBuilder;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

import java.util.Scanner;

public class UpdateCommand implements Command {
    private final Receiver receiver;
    private final Scanner scanner;

    public UpdateCommand(Receiver receiver, Scanner scanner) {
        this.receiver = receiver;
        this.scanner = scanner;
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

            ElementBuilder builder = new ElementBuilder(scanner);
            StudyGroup updatedGroup = builder.createStudyGroup();
            receiver.update(id, updatedGroup);
            return "Study group with ID " + id + " updated successfully!";
        } catch (NumberFormatException e) {
            return "Error: Invalid ID format.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
}