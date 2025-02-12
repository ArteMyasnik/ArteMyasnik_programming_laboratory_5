package lab.laboratory5;

import lab.laboratory5.entity.commands.HelpCommand;

import java.util.HashMap;

public class Invoker {
    private static final HashMap<String, HelpCommand> commands = new HashMap<>();

    static {
        commands.put("help", new HelpCommand());
    }

    public void invoke(String command) {
        System.out.println(commands.get(command).execute());
    }
}
