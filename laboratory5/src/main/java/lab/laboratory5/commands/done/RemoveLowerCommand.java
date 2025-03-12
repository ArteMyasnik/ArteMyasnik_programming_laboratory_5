package lab.laboratory5.commands.done;

import lab.laboratory5.commands.utils.ElementBuilder;
import lab.laboratory5.commands.utils.PassportValidator;
import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.Receiver;

import java.util.Iterator;
import java.util.Scanner;

public class RemoveLowerCommand implements Command {
    private final Receiver receiver;
    private final ElementBuilder elementBuilder;
    private final PassportValidator passportValidator;

    public RemoveLowerCommand(Receiver receiver, ElementBuilder elementBuilder, PassportValidator passportValidator) {
        this.receiver = receiver;
        this.elementBuilder = elementBuilder;
        this.passportValidator = passportValidator;
    }

    @Override
    public String execute(String... arguments) {
        StudyGroup comparisonGroup = elementBuilder.createStudyGroup();

        Iterator<StudyGroup> iterator = receiver.getAll().iterator();
        boolean removed = false;

        while (iterator.hasNext()) {
            StudyGroup group = iterator.next();

            if (group.getName().compareToIgnoreCase(comparisonGroup.getName()) < 0) {
                String passportID = group.getGroupAdmin().getPassportID();
                passportValidator.removePassword(passportID);

                iterator.remove();
                removed = true;
            }
        }

        if (removed) {
            return "All study groups with names lower than '" + comparisonGroup.getName() + "' were removed.";
        } else {
            return "No study groups were removed.";
        }
    }
}