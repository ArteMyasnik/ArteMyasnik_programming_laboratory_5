package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

public class UpdateCommand implements Command {
    private final int id;
    private final Object obj;
    public UpdateCommand(int id, Object obj) {
        this.id = id;
        this.obj = obj;
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return "update";
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
