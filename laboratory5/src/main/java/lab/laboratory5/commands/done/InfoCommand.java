package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;

public class InfoCommand implements Command {
    private final Receiver receiver;

    public InfoCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String... arguments) {
        System.out.println("Collection type: " + receiver.getCollectionType());
        System.out.println("Initialization date: " + receiver.getInitializationDate());
        System.out.println("Number of elements: " + receiver.size());
    }
}