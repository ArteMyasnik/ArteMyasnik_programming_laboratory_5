package lab.laboratory5;

import lab.laboratory5.commands.done.*;

import java.util.HashMap;
import java.util.Scanner;

public class Invoker {
    private final HashMap<String, Command> commands = new HashMap<>();
    private final Receiver receiver;
    private final Scanner scanner;

    public Invoker(Receiver collection, Scanner scanner) {
        this.receiver = collection;
        this.scanner = scanner;
        registerCommands();
    }

    private void registerCommands() {
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand(receiver));
        commands.put("show", new ShowCommand(receiver));
        commands.put("add", new AddCommand(receiver, scanner));
        commands.put("update", new UpdateCommand(receiver, scanner));
        commands.put("remove_by_id", new RemoveByIdCommand(receiver));
        commands.put("clear", new ClearCommand(receiver));
        commands.put("remove_head", new RemoveHeadCommand(receiver));
        commands.put("add_if_max", new AddIfMaxCommand(receiver, scanner));
        commands.put("remove_lower", new RemoveLowerCommand(receiver, scanner));
        commands.put("print_descending", new PrintDescendingCommand(receiver));
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
add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный
filter_less_than_form_of_education formOfEducation : вывести элементы, значение поля formOfEducation которых меньше заданного
print_descending : вывести элементы коллекции в порядке убывания
print_unique_group_admin : вывести уникальные значения поля groupAdmin всех элементов в коллекции
 */