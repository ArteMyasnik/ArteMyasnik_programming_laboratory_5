package com.artemyasnik.command;

import java.util.List;

public final class Commands {
    public static final List<Command> COMMANDS = List.of(
            new Help(),
            new Info(),
            new Show(),
            new Add(),
            new UpdateId(),
            new RemoveById(),
            new Clear(),
            new Save(),
            new ExecuteScript(),
            new Exit(),
            new RemoveHead(),
            new AddIfMax(),
            new RemoveLower(),
            new FilterLessThanFormOfEducation(),
            new PrintDescending(),
            new PrintUniqueGroupAdmin()
    );

    private Commands() {
    }
}
