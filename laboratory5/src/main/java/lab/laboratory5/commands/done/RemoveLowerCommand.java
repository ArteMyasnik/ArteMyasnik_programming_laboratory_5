package lab.laboratory5.commands.done;

import lab.laboratory5.commands.utils.ElementBuilder;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

import java.util.Scanner;

public class RemoveLowerCommand implements Command {
    private final Receiver receiver;
    private final Scanner scanner;

    public RemoveLowerCommand(Receiver receiver, Scanner scanner) {
        this.receiver = receiver;
        this.scanner = scanner;
    }

    @Override
    public String execute(String... arguments) {
        ElementBuilder builder = new ElementBuilder(scanner);
        StudyGroup comparisonGroup = builder.createStudyGroup();

        boolean removed = receiver.getAll().removeIf(
                group -> group.getName().compareToIgnoreCase(comparisonGroup.getName()) < 0
        );

        if (removed) {
            return "All study groups with names lower than '" + comparisonGroup.getName() + "' were removed.";
        } else {
            return "No study groups were removed.";
        }
    }
}