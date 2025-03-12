package lab.laboratory5.commands.done;

import lab.laboratory5.commands.done.Command;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;
import lab.laboratory5.commands.utils.ElementBuilder;

public class AddCommand implements Command {
    private final Receiver receiver;
    private final ElementBuilder elementBuilder;

    public AddCommand(Receiver receiver, ElementBuilder elementBuilder) {
        this.receiver = receiver;
        this.elementBuilder = elementBuilder;
    }

    @Override
    public String execute(String... arguments) {
        StudyGroup studyGroup = elementBuilder.createStudyGroup();
        receiver.add(studyGroup);return "Study group added successfully!";
    }
}