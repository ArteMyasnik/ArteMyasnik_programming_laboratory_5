package lab.laboratory5;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error, please, enter the file name");
            return;
        }
        final String fileName = args[0];
        Scanner scanner  = new Scanner(System.in);
        Invoker invoker = new Invoker();
        System.out.print("$ ");
        while (true) {
            String line  = scanner.nextLine().trim();

            String[] parts = line.split(" ");
            String command = parts[0];
            String[] arguments = new String[parts.length - 1];
            System.arraycopy(parts, 1, arguments, 0, parts.length - 1);

            if (line.isEmpty()) {
                continue;
            }
            if (command.equals("exit")) {
                System.out.println("Exiting...");
                break;
            }

            invoker.invoke(command, arguments);
            System.out.print("$ ");
        }
        scanner.close();
    }
}