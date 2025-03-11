package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;

public class RemoveByIdCommand implements Command {
    private final Receiver receiver;

    public RemoveByIdCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String execute(String... arguments) {
        if (arguments.length == 0) {
            return "Error: ID is required.";
            }

        try {
            int id = Integer.parseInt(arguments[0]);
            receiver.removeById(id);
            return "Study group with ID " + id + " removed successfully!";
        } catch (NumberFormatException e) {
            return "Error: Invalid ID format.";
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
}