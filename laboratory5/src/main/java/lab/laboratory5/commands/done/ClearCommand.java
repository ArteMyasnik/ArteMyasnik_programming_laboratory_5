package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;

public class ClearCommand implements Command {
    private final Receiver receiver;

    public ClearCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public String execute(String... arguments) {
        receiver.clear();
        return "Collection cleared successfully!";
    }
}