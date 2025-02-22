package lab.laboratory5.commands.done;

public interface Command {
    String getName();
    String execute();
    String execute(String ... arguments);
}
