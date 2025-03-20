package lab.laboratory5.commands.utils;

import lab.laboratory5.commands.done.PassportValidator;
import lab.laboratory5.entity.*;
import lombok.Getter;

import java.util.Scanner;

public class ElementBuilder {
    private final Scanner scanner;
    @Getter
    private final PassportValidator passportValidator;

    public ElementBuilder(Scanner scanner, PassportValidator passportValidator) {
        this.scanner = scanner;
        this.passportValidator = passportValidator;
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
        System.out.print("Enter group name: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Group name cannot be empty. Try again: ");
            name = scanner.nextLine().trim();
        }
        return name;
    }

    private Coordinates inputCoordinates() {
        System.out.println("Enter coordinates:");

        System.out.print("Enter X coordinate (floating-point number): ");
        Double x = readDouble();

        System.out.print("Enter Y coordinate (integer, greater than -365): ");
        long y = readLongGreaterThan(-365);

        return new CoordinatesBuilder()
                .x(x)
                .y(y)
                .build();
    }

    private int inputTransferredStudents() {
        System.out.print("Enter the number of transferred students (must be greater than 0): ");
        int transferredStudents = readInt();
        while (transferredStudents <= 0) {
            System.out.print("The number of transferred students must be greater than 0. Try again: ");
            transferredStudents = readInt();
        }
        return transferredStudents;
    }

    private Semester inputSemesterEnum() {
        System.out.print("Choose semester (" + getEnumValues(Semester.values()) + "): ");
        return inputEnum(Semester.values(), "semester");
    }

    private Person inputGroupAdmin() {
        System.out.println("Enter group admin details:");

        System.out.print("Enter admin name: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("Admin name cannot be empty. Try again: ");
            name = scanner.nextLine().trim();
        }

        System.out.print("Enter passport ID: ");
        String passportID = scanner.nextLine().trim();
        while (true) {
            if (passportID.isEmpty()) {
                System.out.print("Passport ID cannot be empty. Try again: ");
            } else if (!passportValidator.isPasswordUnique(passportID)) {
                System.out.print("Passport ID must be unique. Try again: ");
            } else {
                break;
            }
            passportID = scanner.nextLine().trim();
        }

        passportValidator.addPassword(passportID);

        System.out.print("Choose eye color (" + getEnumValues(lab.laboratory5.entity.colors.eye.Color.values()) + "): ");
        lab.laboratory5.entity.colors.eye.Color eyeColor = inputEnum(lab.laboratory5.entity.colors.eye.Color.values(), "eye color");

        System.out.print("Choose hair color (" + getEnumValues(lab.laboratory5.entity.colors.hair.Color.values()) + "): ");
        lab.laboratory5.entity.colors.hair.Color hairColor = inputEnum(lab.laboratory5.entity.colors.hair.Color.values(), "hair color");

        System.out.print("Choose nationality (" + getEnumValues(Country.values()) + "): ");
        Country nationality = inputEnum(Country.values(), "nationality");

        return new Person(name, passportID, eyeColor, hairColor, nationality);
    }

    private Integer inputStudentsCount() {
        System.out.print("Enter the number of students (must be greater than 0, or leave empty): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null;
        }
        int studentsCount = Integer.parseInt(input);
        while (studentsCount <= 0) {
            System.out.print("The number of students must be greater than 0. Try again: ");
            studentsCount = Integer.parseInt(scanner.nextLine().trim());
        }
        return studentsCount;
    }

    private FormOfEducation inputFormOfEducation() {
        System.out.print("Choose form of education (" + getEnumValues(FormOfEducation.values()) + "): ");
        return inputEnum(FormOfEducation.values(), "form of education");
    }

    // helpful methods-------------------------------------------------------------------------------------------------
    private int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter an integer: ");
            }
        }
    }

    private Double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a floating-point number: ");
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
                    System.out.print("Value must be greater than " + min + ". Try again: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter an integer: ");
            }
        }
    }

    private <T extends Enum<T>> T inputEnum(T[] values, String fieldName) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return Enum.valueOf((Class<T>) values.getClass().getComponentType(), input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid input. Choose " + fieldName + " (" + getEnumValues(values) + "): ");
            }
        }
    }

    private <T extends Enum<T>> String getEnumValues(T[] values) {
        StringBuilder sb = new StringBuilder();
        for (T value : values) {
            sb.append(value).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}