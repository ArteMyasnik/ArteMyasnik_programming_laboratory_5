package lab.laboratory5;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
        Scanner scanner  = new Scanner(System.in);
        Invoker invoker = new Invoker();
        while (true) {
            System.out.print("$");
            String line  = scanner.nextLine();
            invoker.invoke(line);
        }
    }
}