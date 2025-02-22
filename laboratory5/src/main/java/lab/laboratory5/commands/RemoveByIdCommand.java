package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

public class RemoveByIdCommand implements Command {
    private final int id;
    public RemoveByIdCommand(int id) {
        this.id = id;
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return "remove_by_id id";
    }

    /**
     * @param arguments
     * @return
     */
    @Override
    public String execute(String... arguments) {
        return "";
    }
}
