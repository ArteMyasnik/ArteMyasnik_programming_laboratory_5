package lab.laboratory5.commands;

import lab.laboratory5.commands.done.Command;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadCommand implements Command {

    /**
     * @return
     */
    @Override
    public String getName() {
        return "read";
    }

    /**
     * @param arguments
     * @return
     */
    @Override
    public String execute(String... arguments) {
        final String fileName = arguments[0];
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName));
             BufferedReader reader = new BufferedReader(isr)) {

            System.out.println("Содержимое файла " + fileName + ":");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
        return "";
    }
}
