package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;

public class RemoveByIdCommand implements Command {
    private final Receiver receiver;

    public RemoveByIdCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String... arguments) {
        if (arguments.length == 0) {
            System.out.println("Error: ID is required.");
            return;
        }

        try {
            int id = Integer.parseInt(arguments[0]);
            receiver.removeById(id);
            System.out.println("Study group with ID " + id + " removed successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid ID format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}