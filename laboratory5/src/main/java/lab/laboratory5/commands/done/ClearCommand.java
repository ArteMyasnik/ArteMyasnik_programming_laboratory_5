package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;

public class ClearCommand implements Command {
    private final Receiver receiver;

    public ClearCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute(String... arguments) {
        receiver.clear();
        System.out.println("Collection cleared successfully!");
    }
}