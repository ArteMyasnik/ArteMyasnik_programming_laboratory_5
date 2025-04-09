package com.artemyasnik.command;

import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;
import lombok.Getter;

@Getter
public abstract class Command {
    public static final String[] EMPTY_ARGS = new String[0];

    private final String name;
    private final String[] args;
    private final String help;
    private final int elementRequired;

    protected Command(final String name, final String[] args, final String help, final int elementRequired) {
        this.name = name;
        this.args = args;
        this.help = help;
        this.elementRequired = elementRequired;
    }

    public Command(final String name, final String[] args, final String help) {
        this(name, args, help, 0);
    }

    public Command(final String name, final String help) {
        this(name, EMPTY_ARGS, help, 0);
    }

    public abstract Response execute(Request request);
}
