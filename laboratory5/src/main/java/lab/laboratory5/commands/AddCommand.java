package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

public class AddCommand implements Command {
    private final Object obj;
    public AddCommand(Object obj) {
        this.obj = obj;
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return "add {element}";
    }

    @Override
    public String execute() {

        return "";
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
