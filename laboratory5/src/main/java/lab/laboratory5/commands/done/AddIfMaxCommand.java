package lab.laboratory5.commands.done;

import lab.laboratory5.commands.utils.ElementBuilder;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

import java.util.Comparator;
import java.util.Scanner;

public class AddIfMaxCommand implements Command {
    private final Receiver receiver;
    private final Scanner scanner;

    public AddIfMaxCommand(Receiver receiver, Scanner scanner) {
        this.receiver = receiver;
        this.scanner = scanner;
    }

    @Override
    public String execute(String... arguments) {
        ElementBuilder builder = new ElementBuilder(scanner);
        StudyGroup newGroup = builder.createStudyGroup();

        StudyGroup maxGroup = receiver.getAll().stream()
                .max(Comparator.comparing(StudyGroup::getName, String.CASE_INSENSITIVE_ORDER))
                .orElse(null);

        if (maxGroup == null || newGroup.getName().compareToIgnoreCase(maxGroup.getName()) > 0) {
            receiver.add(newGroup);
            return "Study group added successfully!";
        } else {
            return "Study group not added: its name is not greater than the maximum.";
        }
    }
}