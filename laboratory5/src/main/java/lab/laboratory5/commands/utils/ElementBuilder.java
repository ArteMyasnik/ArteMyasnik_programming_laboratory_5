package lab.laboratory5.commands.utils;

import lab.laboratory5.entity.*;
import java.util.Scanner;

public class ElementBuilder {
    private final Scanner scanner;

    public ElementBuilder(Scanner scanner) {
        this.scanner = scanner;
    }

    public StudyGroup createStudyGroup() {
        StudyGroupBuilder builder = new StudyGroupBuilder()
                .name(inputName())
                .coordinates(inputCoordinates())
                .transferredStudents(inputTransferredStudents())
                .semesterEnum(inputSemesterEnum())
                .groupAdmin(inputGroupAdmin())
                .studentsCount(inputStudentsCount())
                .formOfEducation(inputFormOfEducation());
        return builder.build();
    }

    private String inputName() {
        System.out.print("Введите название группы: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Название группы не может быть пустым. Попробуйте снова: ");
            name = scanner.nextLine().trim();
        }
        return name;
    }

    private Coordinates inputCoordinates() {
        System.out.println("Введите координаты:");

        System.out.print("Введите координату X (число с плавающей точкой): ");
        Double x = readDouble();

        System.out.print("Введите координату Y (целое число, больше -365): ");
        long y = readLongGreaterThan(-365);

        return new CoordinatesBuilder()
                .x(x)
                .y(y)
                .build();
    }

    private int inputTransferredStudents() {
        System.out.print("Введите количество переведенных студентов (должно быть больше 0): ");
        int transferredStudents = readInt();
        while (transferredStudents <= 0) {
            System.out.print("Количество переведенных студентов должно быть больше 0. Попробуйте снова: ");
            transferredStudents = readInt();
        }
        return transferredStudents;
    }

    private Semester inputSemesterEnum() {
        System.out.println("Доступные семестры:");
        for (Semester semester : Semester.values()) {
            System.out.println(semester.ordinal() + 1 + ". " + semester);
        }

        System.out.print("Выберите номер семестра: ");
        int choice = readIntInRange(1, Semester.values().length);
        return Semester.values()[choice - 1];
    }

    private Person inputGroupAdmin() {
        System.out.print("Введите имя администратора группы: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Имя администратора группы не может быть пустым. Попробуйте снова: ");
            name = scanner.nextLine().trim();
        }

        System.out.print("Введите возраст администратора группы (целое число): ");
        int age = readInt();

        return new Person(name, age);
    }

    private Integer inputStudentsCount() {
        System.out.print("Введите количество студентов (должно быть больше 0, или оставьте пустым): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        int studentsCount = Integer.parseInt(input);
        while (studentsCount <= 0) {
            System.out.print("Количество студентов должно быть больше 0. Попробуйте снова: ");
            studentsCount = Integer.parseInt(scanner.nextLine().trim());
        }
        return studentsCount;
    }

    private FormOfEducation inputFormOfEducation() {
        System.out.println("Доступные формы обучения:");
        for (FormOfEducation form : FormOfEducation.values()) {
            System.out.println(form.ordinal() + 1 + ". " + form);
        }

        System.out.print("Выберите номер формы обучения (или оставьте пустым): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        int choice = Integer.parseInt(input);
        while (choice < 1 || choice > FormOfEducation.values().length) {
            System.out.print("Некорректный выбор. Попробуйте снова: ");
            choice = Integer.parseInt(scanner.nextLine().trim());
        }
        return FormOfEducation.values()[choice - 1];
    }

    // Вспомогательные методы для ввода чисел
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Некорректный ввод. Введите целое число: ");
            }
        }
    }

    private Double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Некорректный ввод. Введите число с плавающей точкой: ");
            }
        }
    }

    private long readLongGreaterThan(long min) {
        while (true) {
            try {
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value > min) {
                    return value;
                } else {
                    System.out.print("Значение должно быть больше " + min + ". Попробуйте снова: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Некорректный ввод. Введите целое число: ");
            }
        }
    }

    private int readIntInRange(int min, int max) {
        int value;
        while (true) {
            value = readInt();
            if (value >= min && value <= max) {
                return value;
            }
            System.out.print("Некорректный выбор. Введите число от " + min + " до " + max + ": ");
        }
    }
}