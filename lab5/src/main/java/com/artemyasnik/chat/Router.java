package com.artemyasnik.chat;

import com.artemyasnik.command.Command;
import com.artemyasnik.command.Commands;
import com.artemyasnik.db.dto.UserDTO;
import com.artemyasnik.io.transfer.Request;
import com.artemyasnik.io.transfer.Response;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Router {
    private static Router instance;

    private Router() {
    }

    public static Router getInstance() {
        return instance == null ? instance = new Router() : instance;
    }

    public Response route(Request request) {
        if (request == null || request.command() == null || request.command().isBlank()) return Response.empty(Objects.requireNonNull(request).userDTO());
        if (request.command().equals("help")) return getHelp(request.userDTO());

        return Commands.COMMANDS.stream()
                .filter(tmp -> tmp.getName().equalsIgnoreCase(request.command()))
                .findFirst()
                .map(command -> command.execute(request))
                .orElse(new Response("Unknown command %s. Type \"help\" for more information ".formatted(request.command()), request.userDTO()));
    }

    private Response getHelp(UserDTO userDTO) {
        return new Response("Available commands: %s".formatted(
                Commands.COMMANDS.stream()
                        .map(command -> "%n - %s (%s) - %s".formatted(
                                command.getName(),
                                Arrays.toString(command.getArgs()),
                                command.getHelp()
                        ))
                        .collect(Collectors.joining())
        ), userDTO);
    }

    public int getElementRequired(String command) {
        return Commands.COMMANDS.stream()
                .filter(temp -> temp.getName().equalsIgnoreCase(command)).findFirst()
                .map(Command::getElementRequired).orElse(0);
    }
}
