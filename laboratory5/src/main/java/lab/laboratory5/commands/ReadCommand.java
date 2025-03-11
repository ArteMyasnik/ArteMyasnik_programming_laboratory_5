package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadCommand implements Command {
    /**
     * @param arguments
     * @return
     */
    @Override
    public String execute(String... arguments) {
        final String fileName = arguments[0];
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName));
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder result = new StringBuilder("Содержимое файла " + fileName + ":");
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (IOException e) {
            return "Ошибка при чтении файла: " + e.getMessage();
        }
    }
}
