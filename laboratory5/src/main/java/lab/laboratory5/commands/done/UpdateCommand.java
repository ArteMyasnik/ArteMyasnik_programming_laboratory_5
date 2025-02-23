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
    public void execute(String... arguments) {
        if (arguments.length == 0) {
            System.out.println("Error: ID is required.");
            return;
        }

        try {
            int id = Integer.parseInt(arguments[0]);
            StudyGroup existingGroup = receiver.getById(id);
            if (existingGroup == null) {
                System.out.println("Error: Study group with ID " + id + " not found.");
                return;
            }

            ElementBuilder builder = new ElementBuilder(scanner);
            StudyGroup updatedGroup = builder.createStudyGroup();
            receiver.update(id, updatedGroup);
            System.out.println("Study group with ID " + id + " updated successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid ID format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}