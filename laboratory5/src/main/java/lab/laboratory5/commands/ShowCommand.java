package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

public class ShowCommand implements Command {
    /**
     * @return
     */
    @Override
    public String getName() {
        return "show";
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
