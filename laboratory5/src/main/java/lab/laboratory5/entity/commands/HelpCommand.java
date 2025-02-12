package lab.laboratory5.entity.commands;

public class HelpCommand implements Command{

    /**
     * @return
     */
    @Override
    public String getName() {
        return "help";
    }

    /**
     * @return
     */
    @Override
    public String execute() {
        return "a lot of commands";
    }
}
