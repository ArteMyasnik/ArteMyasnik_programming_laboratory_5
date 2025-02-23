package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

import java.io.FileWriter;
import java.io.IOException;

public class SaveCommand implements Command {
    /**
     * @param arguments
     * @return
     */
    @Override
    public void execute(String... arguments) {
        final String fileName = arguments[0];
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("\nНовая строка, добавленная программой.");
            System.out.println("Данные успешно записаны в файл.");
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
//        return "";
    }
}
