package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

public class InfoCommand implements Command {

    /**
     * @return
     */
    @Override
    public String getName() {
        return "info";
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
