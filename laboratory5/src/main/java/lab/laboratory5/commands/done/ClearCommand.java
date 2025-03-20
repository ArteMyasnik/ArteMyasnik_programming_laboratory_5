package lab.laboratory5.commands.done;

import lab.laboratory5.Receiver;

public class ClearCommand implements Command {
    private final Receiver receiver;
    private final PassportValidator passportValidator;

    public ClearCommand(Receiver receiver, PassportValidator passportValidator) {
        this.receiver = receiver;
        this.passportValidator = passportValidator;
    }

    @Override
    public String execute(String... arguments) {
        passportValidator.getPassportValidator().clear();
        receiver.clear();

        return "Collection and passport IDs cleared successfully!";
    }
}