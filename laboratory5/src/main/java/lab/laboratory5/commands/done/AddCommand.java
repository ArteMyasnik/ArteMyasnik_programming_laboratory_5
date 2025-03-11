package lab.laboratory5.commands.done;

import lab.laboratory5.commands.utils.ElementBuilder;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;
import java.util.Scanner;

public class AddCommand implements Command {
    private final Receiver collection;
    private final Scanner scanner;

    public AddCommand(Receiver collection, Scanner scanner) {
        this.collection = collection;
        this.scanner = scanner;
    }

    @Override
    public String execute(String... arguments) {
        ElementBuilder builder = new ElementBuilder(scanner);
        StudyGroup studyGroup = builder.createStudyGroup();
        collection.add(studyGroup);
        return "Study group added successfully!";
    }
}