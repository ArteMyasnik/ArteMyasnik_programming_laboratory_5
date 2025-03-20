package lab.laboratory5.commands.done;

import lab.laboratory5.entity.StudyGroup;
import lab.laboratory5.entity.FormOfEducation;
import lab.laboratory5.Receiver;

import java.util.List;

public class FilterLessThanFormOfEducationCommand implements Command {
    private final Receiver receiver;

    public FilterLessThanFormOfEducationCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String execute(String... arguments) {
        if (arguments.length == 0) {
            return "Error: formOfEducation is required.";
        }

        try {
            FormOfEducation formOfEducation = FormOfEducation.valueOf(arguments[0].toUpperCase());

            List<StudyGroup> filteredGroups = receiver.getAll().stream()
                    .filter(group -> group.getFormOfEducation() != null && group.getFormOfEducation().compareTo(formOfEducation) < 0)
                    .toList();

            if (filteredGroups.isEmpty()) {
                return "No study groups found with formOfEducation less than " + formOfEducation + ".";
            } else {
                StringBuilder result = new StringBuilder("Study groups with formOfEducation less than " + formOfEducation + ":\n");
                for (StudyGroup group : filteredGroups) {
                    result.append(group).append("\n");
                }
                return result.toString();
            }
        } catch (IllegalArgumentException e) {
            return "Error: Invalid formOfEducation. Available values: " + getEnumValues(FormOfEducation.values());
        }
    }

    private String getEnumValues(FormOfEducation[] values) {
        StringBuilder sb = new StringBuilder();
        for (FormOfEducation value : values) {
            sb.append(value).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}