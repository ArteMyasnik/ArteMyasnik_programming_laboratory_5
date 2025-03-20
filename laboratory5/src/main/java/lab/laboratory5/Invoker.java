package lab.laboratory5;

import lab.laboratory5.commands.done.*;
import lab.laboratory5.commands.utils.ElementBuilder;
import lab.laboratory5.commands.done.PassportValidator;

import java.util.HashMap;
import java.util.Scanner;

public class Invoker {
    private final HashMap<String, Command> commands = new HashMap<>();
    private final Receiver receiver;
    private final Scanner scanner;
    private final PassportValidator passportValidator;

    public Invoker(Receiver collection, Scanner scanner) {
        this.receiver = collection;
        this.scanner = scanner;
        this.passportValidator = new PassportValidator();
        registerCommands();
    }

    private void registerCommands() {
        ElementBuilder elementBuilder = new ElementBuilder(scanner, passportValidator);
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand(receiver));
        commands.put("show", new ShowCommand(receiver));
        commands.put("add", new AddCommand(receiver, elementBuilder));
        commands.put("update", new UpdateCommand(receiver, elementBuilder));
        commands.put("remove_by_id", new RemoveByIdCommand(receiver, passportValidator));
        commands.put("clear", new ClearCommand(receiver, passportValidator));
        commands.put("remove_head", new RemoveHeadCommand(receiver, passportValidator));
        commands.put("add_if_max", new AddIfMaxCommand(receiver, elementBuilder));
        commands.put("remove_lower", new RemoveLowerCommand(receiver, elementBuilder, passportValidator));
        commands.put("print_descending", new PrintDescendingCommand(receiver));
        commands.put("print_unique_group_admin", new PrintUniqueGroupAdminCommand(receiver));
        commands.put("filter_less_than_form_of_education", new FilterLessThanFormOfEducationCommand(receiver));
    }

    public void invoke(String command, String... arguments) {
        Command cmd = commands.get(command);
        if (cmd != null) {
            cmd.execute(arguments);
        } else {
            System.out.println("Unknown command: " + command);
        }
    }
}


/*
save : сохранить коллекцию в файл
execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
filter_less_than_form_of_education formOfEducation : вывести элементы, значение поля formOfEducation которых меньше заданного
 */