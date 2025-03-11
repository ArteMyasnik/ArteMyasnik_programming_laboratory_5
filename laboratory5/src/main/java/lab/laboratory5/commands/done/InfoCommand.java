package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;

public class InfoCommand implements Command {
    private final Receiver receiver;

    public InfoCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String execute(String... arguments) {
        return "Collection type: " + receiver.getCollectionType() + "\n" +
                "Initialization date: " + receiver.getInitializationDate() + "\n" +
                "Number of elements: " + receiver.size();
    }
}