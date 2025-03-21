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
    public String execute(String... arguments) {
        final String fileName = arguments[0];
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("\nНовая строка, добавленная программой.");
            return "Данные успешно записаны в файл.";
        } catch (IOException e) {
            return "Ошибка при записи в файл: " + e.getMessage();
        }
    }
}
