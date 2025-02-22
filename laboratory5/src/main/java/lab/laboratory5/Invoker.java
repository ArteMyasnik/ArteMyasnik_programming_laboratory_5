package lab.laboratory5;

import lab.laboratory5.commands.*;
import lab.laboratory5.commands.done.Command;
import lab.laboratory5.commands.done.HelpCommand;

import java.util.HashMap;

public class Invoker {
    private static final HashMap<String, Command> commands = new HashMap<>();

    static {
        commands.put("help", new HelpCommand());
        commands.put("info", new InfoCommand());
        commands.put("show", new ShowCommand());
        commands.put("add", new AddCommand());
        commands.put("update", new UpdateCommand());
        commands.put("remove_by_id", new RemoveByIdCommand());
        commands.put("clear", new ClearCommand());
    }

    public void invoke(String command, String ... arguments) {

        System.out.println(commands.get(command).execute());
    }
}


/*
help : вывести справку по доступным командам
info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении
add {element} : добавить новый элемент в коллекцию
update id {element} : обновить значение элемента коллекции, id которого равен заданному
remove_by_id id : удалить элемент из коллекции по его id
clear : очистить коллекцию
save : сохранить коллекцию в файл
execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
exit : завершить программу (без сохранения в файл)
remove_head : вывести первый элемент коллекции и удалить его
add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный
filter_less_than_form_of_education formOfEducation : вывести элементы, значение поля formOfEducation которых меньше заданного
print_descending : вывести элементы коллекции в порядке убывания
print_unique_group_admin : вывести уникальные значения поля groupAdmin всех элементов в коллекции
 */